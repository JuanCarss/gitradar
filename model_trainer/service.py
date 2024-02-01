import os
import shutil
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path
from deserializers.tokenized_event_deserializer import TokenizedEventDeserializer
from aws.s3 import S3
from aws.aws_client_builder import AwsClientBuilder
from aws.aws_services import AwsService
from events.training_event import TrainingEventBuilder
from aws.dynamodb import DynamoDB
from writer.hugging_face_model_writer import HuggingFaceModelWriter

MODEL_DIR = "/tmp/models"
PACKAGES_DIR = "/tmp/packages"


def handler(event, context):
    endpoint = get_environment_variable("CUSTOM_ENDPOINT_URL")
    bucket_name = get_environment_variable("MODELS_BUCKET_ID")
    table_name = get_environment_variable("DYNAMODB_TABLE_NAME")
    install_packages(PACKAGES_DIR)
    s3_client = build_s3_client(endpoint)
    model, model_ts = load_model(bucket_name, s3_client)
    if is_set_up(event):
        return
    from trainers.hugging_face_model_trainer import HuggingFaceModelTrainer
    tokenized_event = TokenizedEventDeserializer().deserialize(event)
    untrained_model_dir = MODEL_DIR + "/" + model_ts
    inputs = get_inputs(endpoint, table_name, tokenized_event.filename)
    trainer = HuggingFaceModelTrainer(model, untrained_model_dir, "new_model", "cpu").train(inputs)
    save_model(S3(s3_client), trainer, bucket_name)
    empty(untrained_model_dir)
    return create_training_event(tokenized_event, endpoint)


def get_environment_variable(key):
    return os.environ[key]


def install_packages(directory):
    make_dir(directory)
    if not is_empty(directory):
        return
    sys.path.insert(0, directory)
    subprocess.run(['pip', 'install', "--target", directory, '-r', 'requirements.txt'])


def build_s3_client(endpoint_url):
    return AwsClientBuilder().for_service(AwsService.S3).with_endpoint_url(endpoint_url).build()


def load_model(bucket_name, s3_client):
    from loaders.hugging_face_model_loader import HuggingFaceModelLoader
    from aws.s3 import S3
    Path(MODEL_DIR).mkdir(parents=True, exist_ok=True)
    if not is_empty(MODEL_DIR):
        model_ts = os.listdir(MODEL_DIR)[0]
        model = HuggingFaceModelLoader(S3(s3_client), bucket_name, MODEL_DIR).load_from_local(
            MODEL_DIR + "/" + model_ts)
    else:
        model, model_ts = HuggingFaceModelLoader(S3(s3_client), bucket_name, MODEL_DIR).load()
    return model, model_ts


def is_set_up(event):
    return "Task" in event.keys() and event["Task"] == "SetUp"


def get_inputs(endpoint, table_name, filename):
    from deserializers.dynamodb_deserializer import DynamoDBDeserializer
    from readers.hugging_face_inputs_reader import DynamoInputsReader
    dynamo = (AwsClientBuilder()
              .for_service(AwsService.DYNAMODB)
              .with_endpoint_url(endpoint)
              .build())
    return DynamoInputsReader(DynamoDB(dynamo), table_name, DynamoDBDeserializer(512)).read(filename)


def save_model(s3, trainer, bucket_name):
    ts = time.time()
    output_path = build_path(MODEL_DIR, str(ts))
    HuggingFaceModelWriter().write(trainer, output_path)
    for file in os.listdir(output_path):
        s3.upload_object(bucket_name, build_path(output_path, file), build_path(str(ts), file))


def empty(folder):
    Path(folder).mkdir(parents=True, exist_ok=True)
    for filename in os.listdir(folder):
        file_path = os.path.join(folder, filename)
        try:
            if os.path.isfile(file_path) or os.path.islink(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path):
                shutil.rmtree(file_path)
        except Exception as e:
            print('Failed to delete %s. Reason: %s' % (file_path, e))


def create_training_event(tokenized_event, endpoint_url):
    from serializers.tokenized_event_serializer import JsonTrainingEventSerializer
    training_event = (TrainingEventBuilder()
                      .with_ts(datetime.now().timestamp())
                      .with_source("lambda.model_trainer")
                      .with_filename(tokenized_event.filename)
                      .build())
    training_event = JsonTrainingEventSerializer().serialize(training_event)
    events_client = (AwsClientBuilder()
                     .for_service(AwsService.EVENTS)
                     .with_endpoint_url(endpoint_url)
                     .build())
    events_client.put_events(Entries=[training_event])
    return training_event


def is_empty(directory):
    return False if os.listdir(directory) else True


def make_dir(directory):
    Path(directory).mkdir(parents=True, exist_ok=True)


def build_path(folder, filename):
    return folder + "/" + filename

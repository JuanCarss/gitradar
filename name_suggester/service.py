import json
import os
import subprocess
import sys
from datetime import datetime
from pathlib import Path

from aws.aws_client_builder import AwsClientBuilder

from aws.aws_services import AwsService
from deserializers.api_gateway_deserializer import ApiGatewayDeserializer
from events.name_suggestion_event import NameSuggestionEventBuilder
from aws.s3 import S3
from serializers.response_event_serializer import JsonResponseEventSerializer

MODEL_DIR = "/tmp/models"
PACKAGES_DIR = "/tmp/packages"
CACHE_DIR = '/tmp/cache/'


def handler(event, context):
    endpoint = get_environment_variable("CUSTOM_ENDPOINT_URL")
    bucket_name = get_environment_variable("MODELS_BUCKET_ID")
    s3_client = build_s3_client(endpoint)
    set_cache_dir("TRANSFORMERS_CACHE")
    install_packages(PACKAGES_DIR)
    model, model_ts = load_model(bucket_name, s3_client)
    if is_set_up(event):
        return
    from predictor.hugging_face_name_suggester import HuggingFaceNameSuggester
    _input = ApiGatewayDeserializer().deserialize(event)
    results = HuggingFaceNameSuggester(model).get_best_prediction(create_inputs(_input, 5))
    create_name_suggestion_event(endpoint)
    return {
        'statusCode': 200,
        'body': json.dumps(results)
    }


def install_packages(directory):
    make_dir(directory)
    if not is_empty(directory):
        return
    sys.path.insert(0, directory)
    subprocess.run(['pip', 'install', "--target", directory, '-r', 'requirements.txt'])


def build_s3_client(endpoint_url):
    return AwsClientBuilder().for_service(AwsService.S3).with_endpoint_url(endpoint_url).build()


def set_cache_dir(cache):
    Path(CACHE_DIR).mkdir(parents=True, exist_ok=True)
    os.environ[cache] = CACHE_DIR


def load_model(bucket_name, s3_client):
    from loaders.hugging_face_model_loader import HuggingFaceModelLoader
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


def create_inputs(_input, mask_num):
    return [_input.replace("<mask>", "".join(["<mask>" for _ in range(i)])) for i in range(1, mask_num + 1)]


def create_name_suggestion_event(endpoint_url):
    name_suggestion_event = (NameSuggestionEventBuilder()
                      .with_ts(datetime.now().timestamp())
                      .with_source("lambda.name_suggester")
                      .build())
    name_suggestion_event = JsonResponseEventSerializer().serialize(name_suggestion_event)
    events_client = (AwsClientBuilder()
                     .for_service(AwsService.EVENTS)
                     .with_endpoint_url(endpoint_url)
                     .build())
    events_client.put_events(Entries=[name_suggestion_event])
    return name_suggestion_event


def is_empty(directory):
    return False if os.listdir(directory) else True


def get_environment_variable(key):
    return os.environ[key]


def make_dir(directory):
    Path(directory).mkdir(parents=True, exist_ok=True)

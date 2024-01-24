import datetime
import os
from pathlib import Path

Path('/tmp/cache/').mkdir(parents=True, exist_ok=True)
os.environ['TRANSFORMERS_CACHE'] = '/tmp/cache/'

from aws.aws_client_builder import AwsClientBuilder
from aws.aws_services import AwsService
from aws.s3 import S3
from data_masker import DataMasker
from code_upload_event_deserializer import AwsCodeUploadEventDeserializer
from events.tokenized_event import TokenizedEventBuilder
from file_processor import FileProcessor
from hugging_face_tokenizer import HuggingFaceTokenizer
from s3_deserializer import S3Deserializer
from tokenized_event_serializer import JsonTokenizedEventSerializer
from aws.dynamodb import DynamoDB
from dynamo_token_serializer import DynamoTokenSerializer
from java_parser import JavaParser


def handler(event, context):
    endpoint = get_environment_variable("CUSTOM_ENDPOINT_URL")
    bucket_name = get_environment_variable("CODEFILES_BUCKET_ID")
    table_name = get_environment_variable("DYNAMODB_TABLE_NAME")
    upload_event = AwsCodeUploadEventDeserializer().deserialize(event)
    corpus = get_corpus(bucket_name, endpoint, upload_event)
    tokens = get_tokens(corpus)
    upload_tokens(tokens, table_name, upload_event, endpoint)
    return create_tokenized_event(upload_event, endpoint)


def get_environment_variable(key):
    return os.environ[key]


def upload_tokens(tokens, table_name, upload_event, endpoint):
    client = (AwsClientBuilder()
              .for_service(AwsService.DYNAMODB)
              .with_endpoint_url(endpoint)
              .at_region(upload_event.region)
              .build())
    tokens["filename"] = upload_event.filename
    DynamoDB(client).put_item(table_name, DynamoTokenSerializer().serialize(tokens))


def get_corpus(bucket_name, endpoint, upload_event):
    client = (AwsClientBuilder()
              .for_service(AwsService.S3)
              .with_endpoint_url(endpoint)
              .at_region(upload_event.region)
              .build())
    s3_object = S3(client).get_object_from(bucket_name, upload_event.filename)
    return S3Deserializer().read(s3_object)


def get_tokens(corpus: str):
    return FileProcessor(JavaParser(), HuggingFaceTokenizer(DataMasker()), 512).process(corpus)


def create_tokenized_event(upload_event, endpoint):
    tokenized_event = (TokenizedEventBuilder()
                       .with_ts(datetime.datetime.now().timestamp())
                       .with_source("lambda.tokenizer")
                       .with_filename(upload_event.filename)
                       .build())
    tokenized_event = JsonTokenizedEventSerializer().serialize(tokenized_event)
    events_client = ((AwsClientBuilder()
                      .for_service(AwsService.EVENTS))
                     .with_endpoint_url(endpoint)
                     .at_region(upload_event.region)
                     .build())
    events_client.put_events(Entries=[tokenized_event])
    return tokenized_event

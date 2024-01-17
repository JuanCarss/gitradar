import datetime
import os

from aws.aws_client_builder import AwsClientBuilder
from aws.aws_services import AwsService
from aws.s3 import S3
from data_masker import DataMasker
from deserializers.code_upload_event_deserializer import AwsCodeUploadEventDeserializer
from events.tokenized_event import TokenizedEventBuilder
from file_processor import FileProcessor
from method_tokenizer import Tokenizer
from parser import Parser
from readers.s3_reader import S3Reader
from serializers.tokenized_event_serializer import JsonTokenizedEventSerializer
from aws.dynamodb import DynamoDB
from tokenizer.serializers.token_serializer import DynamoTokenSerializer


def handler(event):
    endpoint = get_environment_variable("CUSTOM_ENDPOINT_URL")
    bucket_name = get_environment_variable("CODEFILES_BUCKET_ID")
    table_name = get_environment_variable("DYNAMODB_TABLE_NAME")
    upload_event = AwsCodeUploadEventDeserializer().deserialize(event)
    corpus = get_corpus(bucket_name, endpoint, upload_event)
    tokens = get_tokens(corpus)
    upload_tokens(tokens, table_name, upload_event, endpoint)
    return create_tokenized_event(upload_event)


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
    return S3Reader().read(s3_object)


def get_tokens(corpus):
    return FileProcessor(Parser(), Tokenizer(DataMasker())).process(corpus)


def create_tokenized_event(upload_event):
    tokenized_event = (TokenizedEventBuilder()
                       .with_ts(datetime.datetime.now().timestamp())
                       .with_source("lambda.tokenizer")
                       .with_filename(upload_event.filename)
                       .build())
    return JsonTokenizedEventSerializer().serialize(tokenized_event)

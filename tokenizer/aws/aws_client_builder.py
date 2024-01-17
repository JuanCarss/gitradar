import boto3

from tokenizer.aws.aws_services import AwsService


class AwsClientBuilder:
    def __init__(self):
        self.service = None
        self.access_key = None
        self.secret_key = None
        self.endpoint_url = None
        self.region = None

    def for_service(self, service: AwsService):
        self.service = service.value
        return self

    def with_access_key(self, access_key: str):
        self.access_key = access_key
        return self

    def with_secret_key(self, secret_key: str):
        self.secret_key = secret_key
        return self

    def at_region(self, region_name: str):
        self.region = region_name
        return self

    def with_endpoint_url(self, endpoint_url: str):
        self.endpoint_url = endpoint_url
        return self

    def build(self):
        return boto3.client(
            service_name=self.service,
            aws_access_key_id=self.access_key,
            aws_secret_access_key=self.secret_key,
            endpoint_url=self.endpoint_url,
            region_name=self.region)

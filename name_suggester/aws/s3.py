DELIMITER = "/"


class S3:
    def __init__(self, client):
        self.client = client

    def list_prefixes(self, bucket_name: str):
        return list(
            set([obj["Key"].split(DELIMITER)[0] for obj in self.client.list_objects(Bucket=bucket_name)["Contents"]]))

    def list_objects_with_prefix(self, bucket_name: str, prefix: str):
        return self.client.list_objects(Bucket=bucket_name, Prefix=prefix)["Contents"]

    def list_objects(self, bucket_name: str):
        return self.client.list_objects(Bucket=bucket_name)

    def get_object_from(self, bucket_name: str, key: str):
        return self.client.get_object(Bucket=bucket_name, Key=key)

    def get_objects_with_prefix(self, prefix: str, bucket_name: str):
        return [self.get_object_from(bucket_name, obj["Key"])
                for obj in self.client.list_objects(Bucket=bucket_name)["Contents"] if obj["Key"].startswith(prefix)]

    def download_object(self, bucket_name: str, key: str, destination: str):
        self.client.download_file(Filename=destination + DELIMITER + self.__delete_prefix(key), Bucket=bucket_name,
                                  Key=key)

    def upload_object(self, bucket_name: str, source: str, destination: str):
        self.client.upload_file(source, bucket_name, destination)

    def __delete_prefix(self, key):
        return key.split(DELIMITER)[1]

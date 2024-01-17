class S3:
    def __init__(self, client):
        self.client = client

    def get_object_from(self, bucket_name: str, key: str):
        return self.client.get_object(bucket_name, key)

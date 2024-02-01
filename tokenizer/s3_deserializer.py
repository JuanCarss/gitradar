
class S3Deserializer:
    def read(self, source):
        return source['Body'].read().decode("utf-8").replace('\r', '\n')

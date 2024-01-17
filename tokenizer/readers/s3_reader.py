from tokenizer.reader import Reader


class S3Reader(Reader):
    def read(self, source):
        return source['Body'].read()

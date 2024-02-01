from pathlib import Path

from transformers import AutoModelForMaskedLM

from aws.s3 import S3


class HuggingFaceModelLoader:
    def __init__(self, s3: S3, bucket_name: str, destination: str):
        self.s3 = s3
        self.bucket_name = bucket_name
        self.destination = destination

    def load(self):
        model_ts = self.__import_model(self.__get_latest_model_timestamp())
        return AutoModelForMaskedLM.from_pretrained(self.destination + "/" + str(model_ts), local_files_only=True), model_ts

    def load_from_local(self, _dir):
        return AutoModelForMaskedLM.from_pretrained(_dir, local_files_only=True)
    def __get_latest_model_timestamp(self):
        return max(self.s3.list_prefixes(self.bucket_name))

    def __import_model(self, ts):
        self.__create_model_dir(self.destination + "/" + str(ts))
        for obj in self.s3.list_objects_with_prefix(self.bucket_name, ts):
            self.s3.download_object(self.bucket_name, obj["Key"], self.destination + "/" + str(ts))
        return ts

    def __create_model_dir(self, _dir):
        Path(_dir).mkdir(parents=True, exist_ok=True)

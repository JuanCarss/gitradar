from datetime import datetime

from tokenizer.events.code_upload_event import CodeUploadEvent
from tokenizer.deserializer import Deserializer


class AwsCodeUploadEventDeserializer(Deserializer):
    def deserialize(self, _object: str):
        ts = self.__to_timestamp(_object["time"])
        source = _object["source"]
        account = _object["account"]
        region = _object["region"]
        filename = _object["detail"]["filename"]
        return CodeUploadEvent(ts, source, account, region, filename)

    def __to_timestamp(self, time):
        return datetime.strptime(time, "%Y-%m-%dT%H:%M:%SZ").timestamp()

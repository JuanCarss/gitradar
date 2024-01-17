from datetime import datetime

from tokenizer.events.code_upload_event import CodeUploadEvent
from tokenizer.event_deserializer import EventDeserializer


class AwsCodeUploadEventDeserializer(EventDeserializer):
    def deserialize(self, event: str):
        ts = self.__to_timestamp(event["time"])
        source = event["source"]
        account = event["account"]
        region = event["region"]
        filename = event['detail']['filename']
        return CodeUploadEvent(ts, source, account, region, filename)

    def __to_timestamp(self, time):
        return datetime.strptime(time, "%Y-%m-%dT%H:%M:%SZ").timestamp()

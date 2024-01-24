from datetime import datetime

from events.tokenized_event import TokenizedEvent


class TokenizedEventDeserializer:
    def deserialize(self, raw_event: dict):
        ts = self.__to_timestamp(raw_event["time"])
        source = raw_event["source"]
        filename = raw_event["detail"]["Filename"]
        return TokenizedEvent(ts, source, filename)

    def __to_timestamp(self, time):
        return datetime.strptime(time, "%Y-%m-%dT%H:%M:%SZ").timestamp()

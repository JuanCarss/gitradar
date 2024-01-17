from datetime import datetime
import json

from tokenizer.serializer import Serializer


class JsonTokenizedEventSerializer(Serializer):
    def serialize(self, event):
        return json.dumps([{"Time": self.__ts_to_string(event),
                            "Source": event.source,
                            "Detail": self.__generate_detail(event)}], indent=2)

    def __ts_to_string(self, event):
        return datetime.utcfromtimestamp(event.ts).strftime('%Y-%m-%dT%H:%M:%SZ')

    def __generate_detail(self, event):
        return json.dumps({"event-type": "CodeTokenized", "filename": event.filename})

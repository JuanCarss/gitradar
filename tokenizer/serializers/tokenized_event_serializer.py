from datetime import datetime
import json

from tokenizer.event_serializer import EventSerializer


class JsonTokenizedEventSerializer(EventSerializer):
    def serialize(self, event):
        return json.dumps([{"Time": self.__ts_to_string(event),
                            "Source": event.source,
                            "Detail": self.__generate_detail(event)}], indent=2)

    def __generate_detail(self, event):
        return json.dumps({"event-type": "CodeTokenized", "filename": event.filename})

    def __ts_to_string(self, event):
        return datetime.utcfromtimestamp(event.ts).strftime('%Y-%m-%dT%H:%M:%SZ')

from datetime import datetime
import json


class JsonTokenizedEventSerializer:
    def serialize(self, event):
        return {"Time": self.__ts_to_string(event),
                "Source": event.source,
                "Detail": self.__generate_detail(event)}

    def __ts_to_string(self, event):
        return datetime.utcfromtimestamp(event.ts).strftime('%Y-%m-%dT%H:%M:%SZ')

    def __generate_detail(self, event):
        return json.dumps({"EventType": "CodeTokenized", "Filename": event.filename})

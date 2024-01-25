class NameSuggestionEvent:
    def __init__(self, ts: float, source: str):
        self.ts = ts
        self.source = source


class NameSuggestionEventBuilder:
    def __init__(self):
        self.ts = None
        self.source = None

    def with_ts(self, ts: float):
        self.ts = ts
        return self

    def with_source(self, source: str):
        self.source = source
        return self

    def build(self) -> NameSuggestionEvent:
        return NameSuggestionEvent(ts=self.ts, source=self.source)

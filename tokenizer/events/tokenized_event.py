from tokenizer.event import Event


class TokenizedEvent(Event):
    def __init__(self, ts: float, source: str, filename: str):
        self.ts = ts
        self.source = source
        self.filename = filename


class TokenizedEventBuilder:
    def __init__(self):
        self.ts = None
        self.source = None
        self.filename = None

    def with_ts(self, ts: float):
        self.ts = ts
        return self

    def with_source(self, source: str):
        self.source = source
        return self

    def with_filename(self, filename: str):
        self.filename = filename
        return self

    def build(self) -> TokenizedEvent:
        return TokenizedEvent(ts=self.ts, source=self.source, filename=self.filename)

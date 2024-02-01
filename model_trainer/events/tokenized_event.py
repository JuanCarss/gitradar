class TokenizedEvent:
    def __init__(self, ts: float, source: str, filename: str):
        self.ts = ts
        self.source = source
        self.filename = filename

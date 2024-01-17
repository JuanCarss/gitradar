class CodeUploadEvent:
    def __init__(self, ts: float, source: str, account: str, region: str, filename: str):
        self.ts = ts
        self.source = source
        self.account = account
        self.region = region
        self.filename = filename

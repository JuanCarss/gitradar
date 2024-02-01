class DataMasker:
    mask_token = "<mask>"

    def mask(self, method_name: str, body: str):
        return body.replace(method_name, self.mask_token, 1)

import torch


class DynamoDBDeserializer:
    def __init__(self, chunk_size: int):
        self.chunk_size = chunk_size

    def deserialize(self, item):
        return {key: self.__get_chunks(values) for key, values in item.items() if not self.__is_filename(key)}

    def __is_filename(self, key):
        return key == "filename"

    def __get_chunks(self, values: dict):
        return [self.__to_list(chunk.get("L")) for chunk in values.get("L")]

    def __to_list(self, values: list):
        return torch.tensor([int(value.get("N")) for value in values])

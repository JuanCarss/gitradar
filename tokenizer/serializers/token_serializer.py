from tokenizer.serializer import Serializer


class DynamoTokenSerializer(Serializer):
    def serialize(self, tokens):
        return {"filename": {"S": tokens["filename"]},
                "input_ids": {"L": self.__to_dictionary(tokens["input_ids"])},
                "attention_mask": {"L": self.__to_dictionary(tokens["attention_mask"])},
                "labels": {"L": self.__to_dictionary(tokens["labels"])}}

    def __to_dictionary(self, _list):
        return [{"N": str(value)} for value in _list]

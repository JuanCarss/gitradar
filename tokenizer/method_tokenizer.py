from transformers import AutoTokenizer

from data_masker import DataMasker


class Tokenizer:
    def __init__(self, masker: DataMasker):
        self.tokenizer = AutoTokenizer.from_pretrained("microsoft/codebert-base-mlm")
        self.masker = masker

    def tokenize(self, name: str, body: str):
        tokens = self.tokenizer(body)
        tokens["labels"] = self.__generate_labels(name, body, tokens["input_ids"])
        return tokens

    def __generate_labels(self, name, body, tokens):
        labels = [-100] * len(tokens)
        for i, token in enumerate(self.___get_mask(name, body, tokens)):
            if self.__is_masked(token):
                labels[i] = tokens[i]
                tokens[i] = self.tokenizer.mask_token_id
        return labels

    def ___get_mask(self, name, body, tokens):
        return self.__diff(tokens, self.tokenizer(self.masker.mask(name, body))["input_ids"])

    def __is_masked(self, token):
        return token == 1

    def __diff(self, tokens, masked):
        result = [-10] * len(tokens)
        for i, mask in enumerate(masked):
            if tokens[i] == mask:
                result[i] = 0
                continue
            diff_size = tokens[i:].index(masked[i + 1])
            result[i:i + diff_size] = [1] * diff_size
            break
        return [0 if value == -10 else value for value in result]

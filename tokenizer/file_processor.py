from itertools import chain, zip_longest


class FileProcessor:
    def __init__(self, parser, tokenizer, chunk_size: int):
        self.parser = parser
        self.tokenizer = tokenizer
        self.chunk_size = chunk_size

    def process(self, file):
        tokenized_methods = [self.tokenizer.tokenize(method_name, self.parser.get_method_body(method_name, file))
                             for method_name in self.parser.find_methods(file)]
        return self.__to_chunks(tokenized_methods)

    def __to_chunks(self, tokenized_methods):
        tokens = self.__concatenate(tokenized_methods)
        return self.__generate_chunks(tokens)

    def __concatenate(self, tokenized_methods):
        return {key: list(chain(*[method[key] for method in tokenized_methods]))
                for key in tokenized_methods[0].keys()}

    def __generate_chunks(self, tokens):
        tokens["input_ids"] = self.__chunk(tokens["input_ids"], 1)
        tokens["attention_mask"] = self.__chunk(tokens["attention_mask"], 0)
        tokens["labels"] = self.__chunk(tokens["labels"], -100)
        return tokens

    def __chunk(self, values, fill_value):
        return zip_longest(fillvalue=fill_value, *([iter(values)] * self.chunk_size))

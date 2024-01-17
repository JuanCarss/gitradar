from itertools import chain

from parser import Parser
from method_tokenizer import Tokenizer


class FileProcessor:
    def __init__(self, parser: Parser, tokenizer: Tokenizer):
        self.parser = parser
        self.tokenizer = tokenizer

    def process(self, file):
        tokenized_methods = [self.tokenizer.tokenize(method_name, self.parser.get_method_body(method_name, file))
                             for method_name in self.parser.find_methods(file)]
        return self.__concatenate(tokenized_methods)

    def __concatenate(self, tokenized_methods):
        return {key: list(chain(*[tokenized_method[key] for tokenized_method in tokenized_methods])) for key in tokenized_methods[0].keys()}

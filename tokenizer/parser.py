class Parser:
    def find_methods(self, text: str):
        return [self.__method_name(line) for line in text.split("\n")
                if self.__defines_method(line) and not self.__defines_class(line, text)]

    def get_method_body(self, name: str, text: str):
        lines = text.split("\n")
        start_line = self.__find_method_definition(lines, name)
        body = []
        open_braces, close_braces = 0, 0
        for i, line in enumerate(lines[start_line:]):
            open_braces += line.count("{")
            close_braces += line.count("}")
            if self.__is_start_of_method(i) or self.__is_end_of_method(i, lines, start_line):
                line = line.lstrip()
            body.append(line)
            if close_braces == open_braces:
                break
        return "\n".join(body)

    def __is_end_of_method(self, i, lines, start_line):
        return i == len(lines[start_line:])

    def __is_start_of_method(self, i):
        return i == 0

    def __find_method_definition(self, lines, name):
        for i, line in enumerate(lines):
            if self.__contains(name, line):
                return i
        return 0

    def __method_name(self, line):
        return line[:line.index("(")].split()[-1]

    def __defines_method(self, line):
        return line.endswith("{") and self.__contains("(", line)

    def __defines_class(self, line, text):
        return self.__method_name(line) == self.__get_class_name(text)

    def __contains(self, string, line):
        return line.find(string) != -1

    def __get_class_name(self, string):
        start = string.find("class")
        end = string.find("{", start)
        return string[start:end].split()[1]

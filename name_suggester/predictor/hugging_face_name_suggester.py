from transformers import pipeline, AutoTokenizer


class HuggingFaceNameSuggester:
    def __init__(self, model):
        self.tokenizer = AutoTokenizer.from_pretrained("microsoft/codebert-base-mlm")
        self.model = model
        self.predictor = pipeline("fill-mask", model=self.model, tokenizer=self.tokenizer)

    def get_best_prediction(self, inputs):
        return self.__get_best_result(
            [self.__get_highest_score(self.predictor(_input)) for _input in inputs]
        )

    def __get_highest_score(self, results):
        if self.__is_multimasked(results):
            full_name, average_score = self.__get_best_result_of_multiple_masks(results)
            return {"token_str": full_name, 'score': average_score}
        result = self.__get_best_result(results)
        return {"token_str": result["token_str"], 'score': result["score"]}

    def __is_multimasked(self, results):
        return type(results[0]) == list

    def __get_best_result_of_multiple_masks(self, results):
        best_results_list = [self.__get_best_result(masked_list_result) for masked_list_result in results]
        return ("".join([result["token_str"] for result in best_results_list])), (
            self.__scores_average(best_results_list))

    def __scores_average(self, best_results_list):
        return sum([result["score"] for result in best_results_list]) / len(best_results_list)

    def __get_best_result(self, result_list):
        return max(result_list, key=lambda result: result["score"])

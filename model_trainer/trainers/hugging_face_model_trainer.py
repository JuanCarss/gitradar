from datasets import Dataset
from transformers import TrainingArguments, Trainer


class HuggingFaceModelTrainer:
    def __init__(self, model, model_path, model_name, device):
        self.model = model
        self.model_path = model_path
        self.model_name = model_name
        self.device = device

    def train(self, data):
        training_args = self.__define_training_args()
        trainer = self.__define_trainer(data, training_args)
        trainer.train()
        return trainer

    def __define_training_args(self):
        return TrainingArguments(
            output_dir=self.model_path,
            num_train_epochs=1,
            overwrite_output_dir=True,
            learning_rate=2e-10,
            save_strategy="no"
        )

    def __define_trainer(self, data, training_args):
        return Trainer(
            model=self.model,
            args=training_args,
            train_dataset=Dataset.from_dict(data)
        )

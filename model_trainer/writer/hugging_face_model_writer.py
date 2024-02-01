class HuggingFaceModelWriter:
    def write(self, trainer, output_path):
        trainer.save_model(output_path)

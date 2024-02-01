from aws.dynamodb import DynamoDB
from deserializers.dynamodb_deserializer import DynamoDBDeserializer


class DynamoInputsReader:
    def __init__(self, dynamodb: DynamoDB, table_name: str, deserializer: DynamoDBDeserializer):
        self.dynamodb = dynamodb
        self.table_name = table_name
        self.deserializer = deserializer

    def read(self, filename: str):
        return self.deserializer.deserialize(self.dynamodb.get_item(self.table_name, filename))



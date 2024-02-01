class DynamoDB:
    def __init__(self, client):
        self.client = client

    def get_item(self, table_name: str, filename: str):
        return self.client.get_item(TableName=table_name, Key={"filename": {"S": filename}})["Item"]

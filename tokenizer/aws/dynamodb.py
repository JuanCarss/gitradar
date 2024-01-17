class DynamoDB:
    def __init__(self, client):
        self.client = client

    def put_item(self, table_name: str, item):
        return self.client.put_item(TableName=table_name, Item=item)

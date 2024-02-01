class ApiGatewayDeserializer:
    def deserialize(self, request):
        return request["queryStringParameters"]["input"]

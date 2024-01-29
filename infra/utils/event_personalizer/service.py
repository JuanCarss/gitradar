import json
import os

import boto3


events = boto3.client('events',endpoint_url=os.environ["CUSTOM_ENDPOINT_URL"])

def handler(event, context):
    event_type_personalizer = {"Object Deleted": "CodeDeleted", "Object Created": "CodeUploaded"}
    if event["source"] == "aws.s3":
        output_event = {
            "Time": event['time'],
            "Source": f"s3.{event['detail']['bucket']['name']}",
            "Detail": f"{{\"EventType\": \"{event_type_personalizer[event['detail-type']]}\",\"Filename\": \"{event['detail']['object']['key']}\"}}",
        }
    else:
        output_event = {
            "Time": event['time'],
            "Source": event["source"],
            "Detail": f"{{\"EventType\": \"{[event['detail-type']]}\"}}",
        }
    events.put_events(Entries=[output_event])


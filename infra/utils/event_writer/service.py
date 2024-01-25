import boto3
import botocore
import os

s3 = boto3.client('s3', endpoint_url=os.environ["CUSTOM_ENDPOINT_URL"])

def handler(event, context):
    print("Event to process:", event)
    if "Filename" in event["detail"]:
        event_line = event["time"] + "\t" + event["detail"]["EventType"] + "\t" + event["detail"]["Filename"] + "\t" + event["region"]
        header = "ts\tEventType\tFilename\tregion"
    else:
        event_line = event["time"] + "\t" + event["detail"]["EventType"] + "\t" + event["region"]
        header = "ts\tEventType\tregion"

    events_filename = event["source"] + ".tsv"
    if event_file_exists(events_filename):
        write_event(events_filename, event_line)
    else:
        create_event_file(events_filename, header, event_line)
    return

def event_file_exists(events_filename):
    try:
        s3.head_object(Bucket=os.environ["EVENTS_BUCKET_ID"], Key=events_filename)
        return True
    except botocore.exceptions.ClientError as e:
        if e.response["Error"]["Code"] == "404":
            return False
        else:
            raise    

def create_event_file(events_filename, header, event_line):
    tsv_content = f"{header}\n{event_line}"
    s3.put_object(Body=tsv_content, Bucket=os.environ["EVENTS_BUCKET_ID"], Key=events_filename)
    print("File",events_filename,"created")
    return

def write_event(events_filename, event_line):
    existing_content = s3.get_object(Bucket=os.environ["EVENTS_BUCKET_ID"], Key=events_filename)['Body'].read().decode('utf-8')
    s3.put_object(Body=f"{existing_content}\n{event_line}", Bucket=os.environ["EVENTS_BUCKET_ID"], Key=events_filename)
    print("File",events_filename,"updated")
    return

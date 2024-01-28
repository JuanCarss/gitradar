package es.ulpgc.aws.eventbridge;

import es.ulpgc.events.ParsedEvent;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

public class EventBridge {
    public static void putEvent(EventBridgeClient client, ParsedEvent parsedEvent) {
        client.putEvents(buildRequest(buildEntry(parsedEvent)));
    }

    private static PutEventsRequest buildRequest(PutEventsRequestEntry entry) {
        return PutEventsRequest.builder()
                .entries(entry)
                .build();
    }

    private static PutEventsRequestEntry buildEntry(ParsedEvent parsedEvent) {
        return PutEventsRequestEntry.builder()
                .time(parsedEvent.ts())
                .source(parsedEvent.source())
                .detail("{\"EventType\": \"FileParsed\", \"Filename\": \"" + parsedEvent.filename() + "\"}")
                .build();
    }
}

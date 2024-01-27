package es.ulpgc;

import es.ulpgc.events.FileAnalyticsEvent;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

public class AWSEventPublisher {


    private final EventBridgeClient eventBridgeClient;

    public AWSEventPublisher(EventBridgeClient eventBridgeClient) {
        this.eventBridgeClient = eventBridgeClient;
    }

    public void publishEvent(FileAnalyticsEvent fileAnalyticsEvent) {
        eventBridgeClient.putEvents(buildRequest(buildEntry(fileAnalyticsEvent)));
    }

    private static PutEventsRequest buildRequest(PutEventsRequestEntry entry) {
        return PutEventsRequest.builder()
                .entries(entry)
                .build();
    }

    private static PutEventsRequestEntry buildEntry(FileAnalyticsEvent fileAnalyticsEvent) {
        return PutEventsRequestEntry.builder()
                .source(fileAnalyticsEvent.source())
                .detail("{\"EventType\": \"FileAnalyzed\", \"Filename\": \"" + fileAnalyticsEvent.filename() + "\"}")
                .build();
    }


}

package es.ulpgc.aws;

import es.ulpgc.events.MetricsEvent;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

public class EventBridge {
    public static void publishEvent(MetricsEvent metricsEvent, EventBridgeClient eventBridgeClient) {
        eventBridgeClient.putEvents(buildRequest(buildEntry(metricsEvent)));
    }

    private static PutEventsRequest buildRequest(PutEventsRequestEntry entry) {
        return PutEventsRequest.builder()
                .entries(entry)
                .build();
    }

    private static PutEventsRequestEntry buildEntry(MetricsEvent metricsEvent) {
        return PutEventsRequestEntry.builder()
                .source(metricsEvent.source())
                .detail("{\"EventType\": \"FileAnalyzed\", \"Filename\": \"" + metricsEvent.filename() + "\"}")
                .build();
    }
}

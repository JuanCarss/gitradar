package es.ulpgc.aws.eventbridge;

import es.ulpgc.events.MetricsEvent;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

public class EventBridge {
    public static void putEvent(EventBridgeClient client, MetricsEvent metricsEvent) {
        client.putEvents(buildRequest(buildEntry(metricsEvent)));
    }

    private static PutEventsRequest buildRequest(PutEventsRequestEntry entry) {
        return PutEventsRequest.builder()
                .entries(entry)
                .build();
    }

    private static PutEventsRequestEntry buildEntry(MetricsEvent metricsEvent) {
        return PutEventsRequestEntry.builder()
                .time(metricsEvent.ts())
                .source(metricsEvent.source())
                .detail("{\"EventType\": \"FileAnalyzed\", \"Filename\": \"" + metricsEvent.filename() + "\"}")
                .build();
    }
}

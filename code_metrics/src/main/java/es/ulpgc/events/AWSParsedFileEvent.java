package es.ulpgc.events;

import java.util.List;

public record AWSParsedFileEvent(String version, String id, String detailType, String source, String account,
                                 String time, String region, List<String> resources,
                                 es.ulpgc.events.AWSParsedFileEvent.Detail detail) {

    public record Detail(String eventType, String filename) {
    }
}

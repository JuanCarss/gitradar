package es.ulpgc;

import es.ulpgc.events.FileAnalyticsEvent;

public interface FileAnalyticsEventSerializer {

    String serialize(FileAnalyticsEvent fileAnalyticsEvent);
}

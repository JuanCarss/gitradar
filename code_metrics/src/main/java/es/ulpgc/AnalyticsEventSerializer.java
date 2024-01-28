package es.ulpgc;

import es.ulpgc.events.MetricsEvent;

public interface AnalyticsEventSerializer {

    String serialize(MetricsEvent metricsEvent);
}

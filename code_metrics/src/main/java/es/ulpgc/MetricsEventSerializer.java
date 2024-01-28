package es.ulpgc;

import es.ulpgc.metrics.Metrics;

public interface MetricsEventSerializer {
    String serialize(Metrics metricsEvent);
}

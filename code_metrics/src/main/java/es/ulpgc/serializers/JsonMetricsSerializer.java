package es.ulpgc.serializers;

import com.google.gson.Gson;
import es.ulpgc.MetricsEventSerializer;
import es.ulpgc.metrics.Metrics;

public class JsonMetricsSerializer implements MetricsEventSerializer {
    @Override
    public String serialize(Metrics metricsEvent) {
        return new Gson().toJson(metricsEvent);
    }
}

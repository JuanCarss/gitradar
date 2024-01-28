package es.ulpgc.metrics;

import java.util.ArrayList;
import java.util.List;

public record Metrics(List<ClassMetrics> classMetricsList) {

    public static class MetricsBuilder {

        private final List<ClassMetrics> classMetricsList = new ArrayList<>();

        public MetricsBuilder addClass(ClassMetrics classMetrics) {
            classMetricsList.add(classMetrics);
            return this;
        }

        public Metrics build() {
            Metrics metrics = new Metrics(new ArrayList<>(classMetricsList));
            cleanBuilder();
            return metrics;
        }

        private void cleanBuilder() {
            classMetricsList.clear();
        }
    }

}


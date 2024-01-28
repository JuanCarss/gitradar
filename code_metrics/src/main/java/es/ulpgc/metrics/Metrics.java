package es.ulpgc.metrics;

import java.util.ArrayList;
import java.util.List;

public record Metrics(List<ClassMetrics> classMetricsList, int nonClassLines) {

    public static class MetricsBuilder {

        private final List<ClassMetrics> classMetricsList = new ArrayList<>();
        private int referencesLines = 0;

        public MetricsBuilder addClass(ClassMetrics classMetrics) {
            classMetricsList.add(classMetrics);
            return this;
        }

        public MetricsBuilder increaseReferenceLines() {
            referencesLines++;
            return this;
        }

        public Metrics build() {
            Metrics metrics = new Metrics(new ArrayList<>(classMetricsList), referencesLines);
            cleanBuilder();
            return metrics;
        }

        private void cleanBuilder() {
            classMetricsList.clear();
        }
    }

}


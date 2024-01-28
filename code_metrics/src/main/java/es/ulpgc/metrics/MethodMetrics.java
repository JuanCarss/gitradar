package es.ulpgc.metrics;

public record MethodMetrics(int lines, int cyclomaticComplexity) {
    public static class MethodMetricsBuilder {
        private int lines = 0;
        private int cyclomaticComplexity = 0;

        public MethodMetricsBuilder increaseLines() {
            lines++;
            return this;
        }

        public MethodMetricsBuilder increaseCyclomaticComplexity() {
            cyclomaticComplexity++;
            return this;
        }

        public MethodMetrics build() {
            MethodMetrics methodMetrics = new MethodMetrics(lines, cyclomaticComplexity);
            reset();
            return methodMetrics;
        }

        private void reset() {
            lines = 0;
            cyclomaticComplexity = 0;
        }
    }
}

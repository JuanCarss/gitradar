package es.ulpgc.analytics;

public record MethodAnalytics(int methodCodeLines, int cyclomaticComplexity) {


    public static class MethodAnalyticsBuilder {

        private int methodCodeLines = 0;
        private int cyclomaticComplexity = 0;

        public MethodAnalyticsBuilder addMethodCodeLine() {
            methodCodeLines++;
            return this;
        }

        public MethodAnalyticsBuilder addCyclomaticComplexity() {
            cyclomaticComplexity++;
            return this;
        }

        public MethodAnalytics build() {
            MethodAnalytics methodAnalytics = new MethodAnalytics(methodCodeLines, cyclomaticComplexity);
            resetBuilder();
            return methodAnalytics;
        }

        private void resetBuilder() {
            methodCodeLines = 0;
            cyclomaticComplexity = 0;
        }
    }
}

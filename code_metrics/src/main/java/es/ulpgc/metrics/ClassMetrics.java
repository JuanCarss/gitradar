package es.ulpgc.metrics;

import java.util.ArrayList;
import java.util.List;

public class ClassMetrics {
    private final int lines;
    private final List<MethodMetrics> methodMetricsList;
    private final int maxMethodLines = getMaxMethodLines(); //TODO meter las cosas como atributos

    public ClassMetrics(int lines, List<MethodMetrics> methodMetricsList) {
        this.lines = lines;
        this.methodMetricsList = methodMetricsList;
    }

    public int getLines() {
        return lines;
    }

    public Integer getMethodsInClass() {
        return methodMetricsList.size();
    }

    public int getMaxMethodLines() {
        return  methodMetricsList.stream()
                .mapToInt(MethodMetrics::lines)
                .max()
                .getAsInt();
    }

    public double getAvgMethodLines() {
        return methodMetricsList.stream()
                .mapToInt(MethodMetrics::lines)
                .average()
                .getAsDouble();
    }

    public int getMaxCyclomaticComplexity() {
        return  methodMetricsList.stream()
                .mapToInt(MethodMetrics::cyclomaticComplexity)
                .max()
                .getAsInt();
    }

    public double getAvgCyclomaticComplexity() {
        return methodMetricsList.stream()
                .mapToInt(MethodMetrics::cyclomaticComplexity)
                .average()
                .getAsDouble();
    }



    public static class ClassMetricsBuilder {
        private final List<MethodMetrics> methodMetricsList = new ArrayList<>();
        private int codeLines = 0;

        public ClassMetricsBuilder addMethod(MethodMetrics methodMetrics) {
            methodMetricsList.add(methodMetrics);
            codeLines += methodMetrics.lines();
            return this;
        }

        public ClassMetricsBuilder addNonMethodLine() {
            codeLines++;
            return this;
        }

        public ClassMetrics build() {
            ClassMetrics classMetrics = new ClassMetrics(codeLines, new ArrayList<>(methodMetricsList));
            cleanBuilder();
            return classMetrics;
        }

        private void cleanBuilder() {
            methodMetricsList.clear();
            codeLines = 0;
        }
    }
}

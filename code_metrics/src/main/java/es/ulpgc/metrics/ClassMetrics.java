package es.ulpgc.metrics;

import java.util.ArrayList;
import java.util.List;

public class ClassMetrics {
    private final int lines;
    private final List<MethodMetrics> methodMetricsList;
    private final int maxMethodLines;
    private final double avgMethodLines;
    private final int maxCyclomaticComplexity;
    private final double avgCyclomaticComplexity;
    private final Integer methodsInClass;

    public ClassMetrics(int lines, List<MethodMetrics> methodMetricsList) {
        this.lines = lines;
        this.methodMetricsList = methodMetricsList;
        maxMethodLines = determineMaxMethodLines();
        avgMethodLines = computeAvgMethodLines();
        maxCyclomaticComplexity = determineMaxCyclomaticComplexity();
        avgCyclomaticComplexity = computeAvgCyclomaticComplexity();
        methodsInClass = countMethodsInClass();
    }

    public int getLines() {
        return lines;
    }

    private Integer countMethodsInClass() {
        return methodMetricsList.size();
    }

    private int determineMaxMethodLines() {
        return  methodMetricsList.stream()
                .mapToInt(MethodMetrics::lines)
                .max()
                .getAsInt();
    }

    private double computeAvgMethodLines() {
        return methodMetricsList.stream()
                .mapToInt(MethodMetrics::lines)
                .average()
                .getAsDouble();
    }

    private int determineMaxCyclomaticComplexity() {
        return  methodMetricsList.stream()
                .mapToInt(MethodMetrics::cyclomaticComplexity)
                .max()
                .getAsInt();
    }

    private double computeAvgCyclomaticComplexity() {
        return methodMetricsList.stream()
                .mapToInt(MethodMetrics::cyclomaticComplexity)
                .average()
                .getAsDouble();
    }

    public List<MethodMetrics> getMethodMetricsList() {
        return methodMetricsList;
    }

    public int getMaxMethodLines() {
        return maxMethodLines;
    }

    public double getAvgMethodLines() {
        return avgMethodLines;
    }

    public int getMaxCyclomaticComplexity() {
        return maxCyclomaticComplexity;
    }

    public double getAvgCyclomaticComplexity() {
        return avgCyclomaticComplexity;
    }

    public Integer getMethodsInClass() {
        return methodsInClass;
    }

    public static class ClassMetricsBuilder {
        private final List<MethodMetrics> methodMetricsList = new ArrayList<>();
        private int lines = 0;

        public ClassMetricsBuilder addMethod(MethodMetrics methodMetrics) {
            methodMetricsList.add(methodMetrics);
            lines += methodMetrics.lines();
            return this;
        }

        public ClassMetricsBuilder addClassFieldLine() {
            lines++;
            return this;
        }

        public ClassMetrics build() {
            ClassMetrics classMetrics = new ClassMetrics(lines, new ArrayList<>(methodMetricsList));
            cleanBuilder();
            return classMetrics;
        }

        private void cleanBuilder() {
            methodMetricsList.clear();
            lines = 0;
        }
    }
}

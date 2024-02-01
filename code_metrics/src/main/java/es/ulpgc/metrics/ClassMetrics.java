package es.ulpgc.metrics;

import java.util.ArrayList;
import java.util.List;

public class ClassMetrics {
    private final String name;
    private final int lines;
    private final int methodsInClass;
    private final int maxMethodLines;
    private final double avgMethodLines;
    private final int maxCyclomaticComplexity;
    private final double avgCyclomaticComplexity;
    private final List<MethodMetrics> methodMetricsList;

    public ClassMetrics(String name, int lines, List<MethodMetrics> methodMetricsList) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public static class ClassMetricsBuilder {
        private final List<MethodMetrics> methodMetricsList = new ArrayList<>();
        private int lines = 0;
        private String name;

        public ClassMetricsBuilder addMethod(MethodMetrics methodMetrics) {
            methodMetricsList.add(methodMetrics);
            lines += methodMetrics.lines();
            return this;
        }

        public ClassMetricsBuilder addClassFieldLine() {
            lines++;
            return this;
        }

        public ClassMetricsBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ClassMetrics build() {
            ClassMetrics classMetrics = new ClassMetrics(name ,lines, new ArrayList<>(methodMetricsList));
            cleanBuilder();
            return classMetrics;
        }

        private void cleanBuilder() {
            methodMetricsList.clear();
            lines = 0;
        }
    }
}

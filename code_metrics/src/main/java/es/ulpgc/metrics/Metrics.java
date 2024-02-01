package es.ulpgc.metrics;

import java.util.ArrayList;
import java.util.List;

public class Metrics {
    private final String filename;
    private final int classCount;
    private final int lines;
    private final int methodCount;
    private final List<ClassMetrics> classMetricsList;

    public Metrics(String filename, int lines, List<ClassMetrics> classMetricsList) {
        this.filename = filename;
        this.lines = lines;
        classCount = (int) classMetricsList.parallelStream().count();
        methodCount = classMetricsList.parallelStream().mapToInt(ClassMetrics::getMethodsInClass).sum();
        this.classMetricsList = classMetricsList;
    }

    public String getFilename() {
        return filename;
    }

    public int getClassCount() {
        return classCount;
    }

    public int getLines() {
        return lines;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public List<ClassMetrics> getClassMetricsList() {
        return classMetricsList;
    }

    public static class MetricsBuilder {
        private String filename;
        private final List<ClassMetrics> classMetricsList = new ArrayList<>();
        private int lines = 0;

        public MetricsBuilder addClass(ClassMetrics classMetrics) {
            classMetricsList.add(classMetrics);
            return this;
        }

        public MetricsBuilder increaseReferenceLines() {
            lines++;
            return this;
        }

        public MetricsBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Metrics build() {
            Metrics metrics = new Metrics(filename, getTotalLines(), new ArrayList<>(classMetricsList));
            cleanBuilder();
            return metrics;
        }

        private int getTotalLines() {
            return lines + classMetricsList.parallelStream()
                    .mapToInt(ClassMetrics::getLines)
                    .sum();
        }

        private void cleanBuilder() {
            classMetricsList.clear();
            lines = 0;
        }
    }
}

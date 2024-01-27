package es.ulpgc.analytics;

import java.util.ArrayList;
import java.util.List;

public class ClassAnalytics {
    private final List<MethodAnalytics> MethodAnalyticsList;
    private final int codeLines;

    public ClassAnalytics(List<MethodAnalytics> MethodAnalyticsList, int codeLines) {
        this.MethodAnalyticsList = MethodAnalyticsList;
        this.codeLines = codeLines;
    }

    public int getCodeLines() {
        return codeLines;
    }

    public Integer getMethodsInClass() {
        return MethodAnalyticsList.size();
    }

    public int getMaxMethodLines() {
        return  MethodAnalyticsList.stream().mapToInt(MethodAnalytics::methodCodeLines).max().getAsInt();
    }

    public double getAvgMethodLines() {
        return MethodAnalyticsList.stream().mapToInt(MethodAnalytics::methodCodeLines).average().getAsDouble();
    }

    public int getMaxCyclomaticComplexity() {
        return  MethodAnalyticsList.stream().mapToInt(MethodAnalytics::cyclomaticComplexity).max().getAsInt();
    }

    public double getAvgCyclomaticComplexity() {
        return MethodAnalyticsList.stream().mapToInt(MethodAnalytics::cyclomaticComplexity).average().getAsDouble();
    }



    public static class ClassAnalyticsBuilder {

        private final List<MethodAnalytics> methodAnalyticsList = new ArrayList<>();
        private int codeLines = 0;

        public ClassAnalyticsBuilder addMethod(MethodAnalytics methodAnalytics) {
            methodAnalyticsList.add(methodAnalytics);
            codeLines += methodAnalytics.methodCodeLines();
            return this;
        }

        public ClassAnalyticsBuilder addNonMethodCodeLine() {
            codeLines++;
            return this;
        }

        public ClassAnalytics build() {
            ClassAnalytics classAnalytics = new ClassAnalytics(new ArrayList<>(methodAnalyticsList), codeLines);
            cleanBuilder();
            return classAnalytics;
        }

        private void cleanBuilder() {
            methodAnalyticsList.clear();
            codeLines = 0;
        }
    }
}

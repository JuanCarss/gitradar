package es.ulpgc.analytics;

import java.util.ArrayList;
import java.util.List;

public record FileAnalytics(List<ClassAnalytics> classAnalyticsList) {

    public static class FileAnalyticsBuilder {

        private final List<ClassAnalytics> classAnalyticsList = new ArrayList<>();

        public FileAnalyticsBuilder addClass(ClassAnalytics classAnalytics) {
            classAnalyticsList.add(classAnalytics);
            return this;
        }

        public  FileAnalytics build() {
            FileAnalytics fileAnalytics = new FileAnalytics(new ArrayList<>(classAnalyticsList));
            cleanBuilder();
            return fileAnalytics;
        }

        private void cleanBuilder() {
            classAnalyticsList.clear();
        }
    }

}


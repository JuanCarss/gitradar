package es.ulpgc.events;

import java.time.Instant;

public record FileAnalyticsEvent(Instant ts, String source, String filename) {
    public static class FileAnalyticsEventBuilder {

        private Instant ts;
        private String source;
        private String filename;

        public FileAnalyticsEventBuilder withTs(Instant ts) {
            this.ts = ts;
            return this;
        }

        public FileAnalyticsEventBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public FileAnalyticsEventBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public FileAnalyticsEvent build() {
            return new FileAnalyticsEvent(this.ts, this.source, this.filename);
        }
    }
}



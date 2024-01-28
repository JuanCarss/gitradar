package es.ulpgc.events;

import java.time.Instant;

public record MetricsEvent(Instant ts, String source, String filename) {
    public static class MetricsEventBuilder {

        private Instant ts;
        private String source;
        private String filename;

        public MetricsEventBuilder withTs(Instant ts) {
            this.ts = ts;
            return this;
        }

        public MetricsEventBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public MetricsEventBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public MetricsEvent build() {
            return new MetricsEvent(this.ts, this.source, this.filename);
        }
    }
}



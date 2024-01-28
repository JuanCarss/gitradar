package es.ulpgc.events;

import java.time.Instant;

public record ParsedEvent(Instant ts, String source, String filename) {
    public static class ParsedEventBuilder {
        private Instant ts;
        private String source;
        private String filename;

        public ParsedEventBuilder withTs(Instant ts) {
            this.ts = ts;
            return this;
        }

        public ParsedEventBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public ParsedEventBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public ParsedEvent build() {
            return new ParsedEvent(this.ts, this.source, this.filename);
        }
    }
}

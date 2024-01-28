package es.ulpgc.events;

import java.time.Instant;

public record ParsedEvent(Instant ts, String source, String filename) {
    public static class ParsedFileEventBuilder {
        private Instant ts;
        private String source;
        private String filename;

        public ParsedFileEventBuilder withTs(Instant ts) {
            this.ts = ts;
            return this;
        }

        public ParsedFileEventBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public ParsedFileEventBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public ParsedEvent build() {
            return new ParsedEvent(this.ts, this.source, this.filename);
        }
    }
}



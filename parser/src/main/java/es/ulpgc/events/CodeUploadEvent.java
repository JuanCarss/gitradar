package es.ulpgc.events;

import java.time.Instant;

public record CodeUploadEvent(Instant ts, String source, String filename) {
}

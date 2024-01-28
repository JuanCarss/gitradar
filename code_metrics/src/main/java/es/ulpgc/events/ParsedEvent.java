package es.ulpgc.events;

import java.time.Instant;

public record ParsedEvent(Instant ts, String source, String filename) {
}



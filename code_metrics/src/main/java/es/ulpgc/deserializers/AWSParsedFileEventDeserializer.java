package es.ulpgc.deserializers;

import com.google.gson.Gson;
import es.ulpgc.ParsedFileEventDeserializer;
import es.ulpgc.events.AWSParsedFileEvent;
import es.ulpgc.events.ParsedFileEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class AWSParsedFileEventDeserializer implements ParsedFileEventDeserializer {

    public ParsedFileEvent deserialize(String json) {
        AWSParsedFileEvent awsParsedFileEvent = new Gson().fromJson(json, AWSParsedFileEvent.class);
        return getParsedFileEvent(awsParsedFileEvent);
    }

    private ParsedFileEvent getParsedFileEvent(AWSParsedFileEvent awsParsedFileEvent) {
        return new ParsedFileEvent.ParsedFileEventBuilder()
                .withTs(getInstant(awsParsedFileEvent.time()))
                .withFilename(awsParsedFileEvent.detail().filename())
                .build();
    }

    private Instant getInstant(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")).toInstant(ZoneOffset.UTC);
    }

}

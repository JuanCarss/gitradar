package es.ulpgc.deserializers;

import com.google.gson.*;
import es.ulpgc.events.ParsedEvent;

import java.lang.reflect.Type;
import java.time.Instant;

public class ParsedEventDeserializer {
    public ParsedEvent deserialize(String event) {
        Gson gson = new GsonBuilder().registerTypeAdapter(ParsedEvent.class, new ParseEventAdapter()).create();
        return gson.fromJson(event, ParsedEvent.class);
    }
    public static class ParseEventAdapter implements JsonDeserializer<ParsedEvent> {
        @Override
        public ParsedEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new ParsedEvent(
                    Instant.parse(jsonObject.get("time").getAsString()),
                    jsonObject.get("source").getAsString(),
                    jsonObject.get("detail").getAsJsonObject().get("Filename").getAsString());
        }
    }
}

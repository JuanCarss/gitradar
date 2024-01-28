package es.ulpgc.deserializers;

import com.google.gson.*;
import es.ulpgc.events.ParsedEvent;

import java.lang.reflect.Type;
import java.time.Instant;

public class ParsedEventDeserializer {
    public ParsedEvent deserialize(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(ParsedEvent.class, new ParseEventTypeAdapter()).create();
        return gson.fromJson(json, ParsedEvent.class);
    }
    public static class ParseEventTypeAdapter implements JsonDeserializer<ParsedEvent> {
        @Override
        public ParsedEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new ParsedEvent(
                    Instant.parse(jsonObject.get("time").getAsString()),
                    jsonObject.get("source").getAsString(),
                    jsonObject.get("detail").getAsJsonObject().get("filename").getAsString());
        }
    }
}
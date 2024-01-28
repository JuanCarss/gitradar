package es.ulpgc.deserializers;

import com.google.gson.*;
import es.ulpgc.events.CodeUploadEvent;

import java.lang.reflect.Type;
import java.time.Instant;

public class CodeUploadEventDeserializer {
    public CodeUploadEvent deserialize(String event) {
        Gson gson = new GsonBuilder().registerTypeAdapter(CodeUploadEvent.class, new CodeUploadTypeAdapter()).create();
        return gson.fromJson(event, CodeUploadEvent.class);
    }

    private static class CodeUploadTypeAdapter implements JsonDeserializer<CodeUploadEvent> {
        @Override
        public CodeUploadEvent deserialize(JsonElement json, Type type, JsonDeserializationContext content) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new CodeUploadEvent(
                    Instant.parse(jsonObject.get("time").getAsString()),
                    jsonObject.get("source").getAsString(),
                    jsonObject.get("detail").getAsJsonObject().get("filename").getAsString()
            );
        }
    }
}

package es.ulpgc.serializers;

import com.google.gson.*;
import es.ulpgc.MetricsEventSerializer;
import es.ulpgc.metrics.ClassMetrics;
import es.ulpgc.metrics.MethodMetrics;
import es.ulpgc.metrics.Metrics;

import java.lang.reflect.Type;
import java.util.List;

public class JsonMetricsSerializer implements MetricsEventSerializer {
    @Override
    public String serialize(Metrics metricsEvent) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Metrics.class, new MetricsAdapter())
                .create();
        return gson.toJson(metricsEvent);
    }

    public static class MetricsAdapter implements JsonSerializer<Metrics> {
        @Override
        public JsonElement serialize(Metrics src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("filename", src.getFilename());
            obj.addProperty("classCount", src.getClassCount());
            obj.addProperty("lines", src.getLines());
            obj.addProperty("methodCount", src.getMethodCount());
            obj.add("classes", serializeClassesMetrics(src.getClassMetricsList()));
            return obj;
        }

        private JsonElement serializeClassesMetrics(List<ClassMetrics> classMetricsList) {
            return classMetricsList.stream()
                    .map(this::serializeClassMetrics)
                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
        }

        private JsonObject serializeClassMetrics(ClassMetrics classMetrics) {
            JsonObject obj = new JsonObject();
            obj.addProperty("identifier", classMetrics.getName());
            obj.addProperty("lines", classMetrics.getLines());
            obj.addProperty("methodsInClass", classMetrics.getMethodsInClass());
            obj.addProperty("maxMethodLines", classMetrics.getMaxMethodLines());
            obj.addProperty("avgMethodLines", classMetrics.getAvgMethodLines());
            obj.addProperty("maxCyclomaticComplexity", classMetrics.getMaxCyclomaticComplexity());
            obj.addProperty("avgCyclomaticComplexity", classMetrics.getAvgCyclomaticComplexity());
            obj.add("methods", serializeMethodMetricsList(classMetrics.getMethodMetricsList()));
            return obj;
        }

        private JsonElement serializeMethodMetricsList(List<MethodMetrics> methodMetricsList) {
            return methodMetricsList.stream()
                    .map(this::serializeMethodMetrics)
                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
        }

        private JsonObject serializeMethodMetrics(MethodMetrics methodMetrics) {
            JsonObject obj = new JsonObject();
            obj.addProperty("identifier", methodMetrics.name());
            obj.addProperty("lines", methodMetrics.lines());
            obj.addProperty("cyclomaticComplexity", methodMetrics.cyclomaticComplexity());
            return obj;
        }
    }
}

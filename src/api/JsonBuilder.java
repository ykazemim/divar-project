package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonBuilder {
    
    public static String toJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) json.append(",");
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            json.append(valueToJson(entry.getValue()));
        }
        
        json.append("}");
        return json.toString();
    }
    
    public static String arrayToJson(Collection<?> collection) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        
        for (Object item : collection) {
            if (!first) json.append(",");
            first = false;
            json.append(valueToJson(item));
        }
        
        json.append("]");
        return json.toString();
    }
    
    private static String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Collection) {
            return arrayToJson((Collection<?>) value);
        } else if (value instanceof Map) {
            return toJson((Map<String, Object>) value);
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }
    
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    public static String error(String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("error", message);
        return toJson(data);
    }
    
    public static String success(String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("message", message);
        return toJson(data);
    }
}

package org.raku.comma.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

class MetaDataJSONSerializer {
    private static final String[] fieldsOrder = new String[]{
            "name", "description", "version", "perl",
            "meta-version", "authors", "auth",
            "depends", "build-depends", "test-depends",
            "provides", "resources",
            "support",  "license", "tags", "api", "production",
            "emulates", "supersedes", "superseded-by", "excludes",
            "source-url"
    };

    static String serializer(JSONObject meta) {
        Map<String, Object> orderedMetaFieldsMap = new LinkedHashMap<>();

        // Go for fields we know in certain order,
        // ignore missing ones
        for (String field : fieldsOrder) {
            if (meta.has(field)) {
                orderedMetaFieldsMap.put(field, processField(meta.get(field)));
            }
        }
        // There could be fields we don't know about,
        // gather them too
        Set<String> customKeys = new HashSet<>(meta.keySet());
        Arrays.asList(fieldsOrder).forEach(customKeys::remove);
        for (String unknownField : customKeys) {
            orderedMetaFieldsMap.put(unknownField, processField(meta.get(unknownField)));
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(orderedMetaFieldsMap, LinkedHashMap.class);
    }

    private static Object processField(Object fieldValue) {
        if (fieldValue instanceof JSONObject) {
            Map<String, Object> map = ((JSONObject)fieldValue).toMap();
            return new TreeMap<>(map);
        }
        else if (fieldValue instanceof JSONArray) {
            return ((JSONArray)fieldValue).toList();
        }
        return fieldValue;
    }
}

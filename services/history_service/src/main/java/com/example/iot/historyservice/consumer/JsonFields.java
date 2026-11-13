package com.example.iot.historyservice.consumer;

import com.fasterxml.jackson.databind.JsonNode;

final class JsonFields {

    private JsonFields() {}

    static String text(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
    }

    static Double number(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value == null || value.isNull()) continue;
            if (value.isNumber()) return value.asDouble();
            try {
                return Double.parseDouble(value.asText());
            } catch (NumberFormatException ignored) {
                // probar el siguiente nombre
            }
        }
        return null;
    }
}
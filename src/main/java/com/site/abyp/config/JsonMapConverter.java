package com.site.abyp.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;


import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = false)
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, PGobject> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public PGobject convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(attribute == null ? "{}" : mapper.writeValueAsString(attribute));
            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException("Could not convert Map to PGobject(JSONB)", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(PGobject dbData) {
        if (dbData == null || dbData.getValue() == null) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(dbData.getValue(),
                    new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert PGobject(JSONB) to Map", e);
        }
    }
}

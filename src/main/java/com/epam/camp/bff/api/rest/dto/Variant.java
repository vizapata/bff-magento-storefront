package com.epam.camp.bff.api.rest.dto;

import lombok.With;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@With
public record Variant(
        Integer id,
        String sku,
        String slug,
        String name,
        List<Map<String, Object>> prices,
        List<Map<String, Object>> attributes,
        List<Map<String, Object>> images) {
    public Variant() {
        this(null, null, null, null, null, null, null);
    }

    public Variant withPrice(Double price) {
        List<Map<String, Object>> prices = List.of(
                Map.of("value", new Price("USD", price * 100))
        );
        return new Variant(id, sku, slug, name, prices, attributes, images);
    }

    public Variant withImage(String image) {
        List<Map<String, Object>> images = List.of(Map.of("url", image));
        return new Variant(id, sku, slug, name, prices, attributes, images);
    }

    public Variant withAttribute(String name, String key, String label) {
        if (key == null || label == null) {
            return this;
        }
        var newAttribute = Map.of("name", name,
                "value", Map.of("key", key, "label", label));
        List<Map<String, Object>> newAttributes = new ArrayList<>();
        if (attributes != null) {
            newAttributes.addAll(attributes);
        }
        newAttributes.add(newAttribute);
        return new Variant(id, sku, slug, this.name, prices, newAttributes, images);
    }
}

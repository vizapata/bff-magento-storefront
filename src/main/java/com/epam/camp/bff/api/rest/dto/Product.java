package com.epam.camp.bff.api.rest.dto;

import java.util.List;
import java.util.Optional;

public record Product(Integer id,
                      String name,
                      String description,
                      String slug,
                      List<Variant> variants,
                      Variant masterVariant,
                      String type) {
    public Double getPrice(){
        return Optional.ofNullable(masterVariant)
                .map(Variant::prices)
                .orElse(List.of())
                .stream()
                .filter(price -> price.containsKey("value"))
                .map(price -> price.get("value"))
                .filter(Price.class::isInstance)
                .map(price -> ((Price) price).centAmount())
                .findFirst()
                .orElse(0d);
    }
}

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
    public Double getPrice() {
        return Optional.ofNullable(masterVariant)
                .map(Variant::getPrice)
                .orElse(0d);
    }
}

package com.epam.camp.bff.api.rest.dto;

import java.util.List;

public record Product(Integer id,
                      String name,
                      String description,
                      String slug,
                      List<Variant> variants,
                      Variant masterVariant) {
}

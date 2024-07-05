package com.epam.camp.bff.api.rest.dto;

import java.util.List;

public record Category(Integer id,
                       String name,
                       String description,
                       String slug,
                       Parent parent,
                       List<Ancestor> ancestors) {
}

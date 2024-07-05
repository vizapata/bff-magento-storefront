package com.epam.camp.bff.api.rest.dto;

import java.util.List;

public record Page<T>(
        List<T> results,
        int total,
        int limit,
        int offset) {
}

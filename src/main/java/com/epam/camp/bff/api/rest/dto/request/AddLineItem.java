package com.epam.camp.bff.api.rest.dto.request;

public record AddLineItem(
        Integer quantity,
        String variantId) {
}

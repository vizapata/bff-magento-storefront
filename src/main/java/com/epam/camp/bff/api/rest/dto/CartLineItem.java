package com.epam.camp.bff.api.rest.dto;

public record CartLineItem(
        String id,
        String productId,
        Variant variant,
        Integer quantity) {
}

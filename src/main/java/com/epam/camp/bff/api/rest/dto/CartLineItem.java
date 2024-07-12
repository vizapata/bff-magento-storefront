package com.epam.camp.bff.api.rest.dto;

public record CartLineItem(
        String id,
        Variant variant,
        Integer quantity,
        Double totalPrice,
        String currencyCode) {
}

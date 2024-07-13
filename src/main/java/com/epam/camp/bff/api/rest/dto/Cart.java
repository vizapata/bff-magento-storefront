package com.epam.camp.bff.api.rest.dto;

import lombok.With;

import java.util.List;

public record Cart(
        @With
        String id,
        String quoteId,
        Integer version,
        String customerId,
        List<CartLineItem> lineItems,
        Price totalPrice,
        Double totalQuantity) {
}

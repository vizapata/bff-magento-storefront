package com.epam.camp.bff.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CartItem(
        @JsonProperty("item_id")
        Integer itemId,
        String sku,
        Integer qty,
        String name,
        Double price,
        @JsonProperty("product_type")
        String productType,
        @JsonProperty("quote_id")
        Integer quoteId) {
}

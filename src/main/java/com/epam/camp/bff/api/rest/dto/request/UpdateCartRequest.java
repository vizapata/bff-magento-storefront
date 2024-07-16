package com.epam.camp.bff.api.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record UpdateCartRequest(
        Integer version,
        CartAction action,
        @JsonAlias("AddLineItem")
        AddLineItem addLineItem,
        @JsonAlias("ChangeLineItemQuantity")
        ChangeLineItemQuantity changeLineItemQuantity
) {
}

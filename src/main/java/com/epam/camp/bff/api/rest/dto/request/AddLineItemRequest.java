package com.epam.camp.bff.api.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AddLineItemRequest(
        Integer version,
        String action,
        @JsonAlias("AddLineItem")
        AddLineItem lineItem
) {
}

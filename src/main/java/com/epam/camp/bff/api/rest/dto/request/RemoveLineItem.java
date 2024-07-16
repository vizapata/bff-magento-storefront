package com.epam.camp.bff.api.rest.dto.request;

public record RemoveLineItem(
        Integer lineItemId,
        Integer quantity) {
}

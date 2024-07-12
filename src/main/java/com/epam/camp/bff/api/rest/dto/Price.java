package com.epam.camp.bff.api.rest.dto;

public record Price(
        String currencyCode,
        Double centAmount) {
}

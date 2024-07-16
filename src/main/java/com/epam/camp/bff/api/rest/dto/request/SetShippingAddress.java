package com.epam.camp.bff.api.rest.dto.request;

public record SetShippingAddress(
        String country,
        String firstName,
        String lastName,
        String streetName,
        String streetNumber,
        String postalCode,
        String city,
        String region,
        String email) {
}

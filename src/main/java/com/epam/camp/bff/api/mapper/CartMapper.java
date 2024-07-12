package com.epam.camp.bff.api.mapper;

import com.epam.camp.bff.api.rest.dto.Cart;

import java.util.Map;

public interface CartMapper {
    Cart toCart(Map<String, Object> data, String id);
}

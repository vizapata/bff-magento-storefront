package com.epam.camp.bff.api.mapper;

import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.CartItem;
import com.epam.camp.bff.api.rest.dto.Product;

import java.util.Map;

public interface CartMapper {
    Cart toCart(Map<String, Object> data, String id);

    CartItem toCartItem(Product product, Integer quantity);
}

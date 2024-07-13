package com.epam.camp.bff.api.mapper.impl;

import com.epam.camp.bff.api.mapper.CartMapper;
import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.CartItem;
import com.epam.camp.bff.api.rest.dto.Price;
import com.epam.camp.bff.api.rest.dto.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CartMapperImpl implements CartMapper {
    @Override
    public Cart toCart(Map<String, Object> data, String id) {
        return new Cart(
                id,
                getProperty(data, "id"),
                0,
                getProperty(data, "customer.id"),
                List.of(),
                new Price("USD", 0d * 100),
                0d);
    }

    @Override
    public CartItem toCartItem(Product product, Integer quantity) {
        return new CartItem(
                null,
                product.masterVariant().sku(),
                quantity,
                product.name(),
                product.getPrice(),
                product.type(),
                null
        );
    }

    private String getProperty(Map<String, Object> data, String key) {
        var keys = key.split("\\.");
        var returnValue = "";
        Map<String, Object> map = data;
        for (String currentKey : keys) {
            if (!map.containsKey(currentKey)) {
                break;
            }

            var currentValue = data.get(currentKey);
            if (currentValue instanceof Map m) {
                map = m;
            } else if (currentValue != null) {
                returnValue = currentValue.toString();
            }
        }
        return returnValue;
    }
}

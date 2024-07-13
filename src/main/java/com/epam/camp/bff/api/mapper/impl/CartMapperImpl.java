package com.epam.camp.bff.api.mapper.impl;

import com.epam.camp.bff.api.mapper.CartMapper;
import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.CartItem;
import com.epam.camp.bff.api.rest.dto.CartLineItem;
import com.epam.camp.bff.api.rest.dto.Price;
import com.epam.camp.bff.api.rest.dto.Product;
import com.epam.camp.bff.api.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartMapperImpl implements CartMapper {
    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @Override
    public Cart toCart(Map<String, Object> data, String id) {
        var items = Optional.ofNullable(data.get("items"))
                .filter(List.class::isInstance)
                .map(list -> (List<Map<String, Object>>) list)
                .orElse(List.of())
                .stream()
                .map(this::toCartLineItem)
                .toList();

        var quantity = items.stream().mapToDouble(CartLineItem::quantity).sum();
        var price = items.stream().mapToDouble(item -> item.variant().getPrice() * item.quantity()).sum();

        return new Cart(
                id,
                getProperty(data, "id"),
                0,
                getProperty(data, "customer.id"),
                items,
                new Price(getProperty(data, "currency.global_currency_code"), price),
                quantity);
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

    @Override
    public CartItem toCartItem(Map<String, Object> data) {
        return objectMapper.convertValue(data, CartItem.class);
    }

    @Override
    public CartLineItem toCartLineItem(Map<String, Object> data) {
        var product = productService.getProductBySku(getProperty(data, "sku"));
        return new CartLineItem(
                getProperty(data, "item_id"),
                product.id().toString(),
                product.masterVariant(),
                Integer.parseInt(getProperty(data, "qty")));
    }

    private String getProperty(Map<String, Object> data, String key) {
        var keys = key.split("\\.");
        var returnValue = "";
        Map<String, Object> map = data;
        for (String currentKey : keys) {
            if (!map.containsKey(currentKey)) {
                break;
            }

            var currentValue = map.get(currentKey);
            if (currentValue instanceof Map m) {
                map = m;
            } else if (currentValue != null) {
                returnValue = currentValue.toString();
            }
        }
        return returnValue;
    }
}

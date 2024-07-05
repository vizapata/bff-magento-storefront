package com.epam.camp.bff.api.mapper;

import com.epam.camp.bff.api.rest.dto.Product;

import java.util.List;
import java.util.Map;

public interface ProductMapper {
    Product toProduct(Map<String, Object> data);

    default List<Product> toProducts(List<Map<String, Object>> products) {
        return products.stream().map(this::toProduct).toList();
    }
}

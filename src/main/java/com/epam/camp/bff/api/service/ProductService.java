package com.epam.camp.bff.api.service;

import com.epam.camp.bff.api.rest.dto.Page;
import com.epam.camp.bff.api.rest.dto.Product;

public interface ProductService {
    Page<Product> listProductsByCategory(Integer categoryId, Integer offset, Integer limit);

    Product getProductBySku(String sku);
}

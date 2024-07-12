package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.mapper.ProductMapper;
import com.epam.camp.bff.api.rest.dto.Page;
import com.epam.camp.bff.api.rest.dto.Product;
import com.epam.camp.bff.api.service.ApiService;
import com.epam.camp.bff.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {
    private static final String PRODUCTS_ENDPOINT_PATH = "V1/products";
    private static final String QUERY_PARAMS_PATTERN = "?searchCriteria[currentPage]=%d" +
            "&searchCriteria[pageSize]=%d" +
            "&searchCriteria[filterGroups][0][filters][0][conditionType]=eq" +
            "&searchCriteria[filterGroups][0][filters][0][field]=category_id" +
            "&searchCriteria[filterGroups][0][filters][0][value]=%d";
    private final ApiService apiService;
    private final ProductMapper productMapper;


    @Override
    public Page<Product> listProductsByCategory(Integer categoryId, Integer offset, Integer limit) {
        log.info("Listing products by category. {id = {}, offset={}, limit={}}", categoryId, offset, limit);
        var url = PRODUCTS_ENDPOINT_PATH + String.format(QUERY_PARAMS_PATTERN, offset, limit, categoryId);
        var result = apiService.getForMap(url);
        List<Map<String, Object>> productsEntry = List.of();
        if (result != null && !result.isEmpty()) {
            productsEntry = (List<Map<String, Object>>) result.get("items");
        }

        var products = productMapper.toProducts(productsEntry);
        return new Page<>(products, products.size(), limit, offset);
    }

    @Override
    public Product getProductBySku(String sku) {
        log.info("Loading product by SKU={}", sku);
        var response = apiService.getForMap(PRODUCTS_ENDPOINT_PATH + "/" + sku);
        return productMapper.toProduct(response);
    }
}

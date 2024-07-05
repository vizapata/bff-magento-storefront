package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.mapper.ProductMapper;
import com.epam.camp.bff.api.rest.dto.Page;
import com.epam.camp.bff.api.rest.dto.Product;
import com.epam.camp.bff.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unchecked")
public class ProductServiceImpl implements ProductService {
    private static final String PRODUCTS_ENDPOINT_PATH = "/V1/products";
    private static final String QUERY_PARAMS_PATTERN = "?searchCriteria[currentPage]={offset}" +
            "&searchCriteria[pageSize]={limit}" +
            "&searchCriteria[filterGroups][0][filters][0][conditionType]=eq" +
            "&searchCriteria[filterGroups][0][filters][0][field]=category_id" +
            "&searchCriteria[filterGroups][0][filters][0][value]={categoryId}";
    private final RestTemplate restTemplate;
    private final ProductMapper productMapper;

    @Value("${app.magento.api.baseUrl}")
    private String apiBaseUrl;
    @Value("${app.magento.api.path}")
    private String apiPath;

    @Override
    public Page<Product> listProductsByCategory(Integer categoryId, Integer offset, Integer limit) {
        var params = Map.of("categoryId", categoryId,
                "offset", offset,
                "limit", limit);
        log.info("Listing products by category. {id = {}, offset={}, limit={}}", categoryId, offset, limit);
        var uri = new DefaultUriBuilderFactory(apiBaseUrl + apiPath + PRODUCTS_ENDPOINT_PATH + QUERY_PARAMS_PATTERN)
                .builder().build(params);

        var result = (Map<String, Object>) restTemplate.getForObject(uri, Map.class);
        List<Map<String, Object>> productsEntry = List.of();
        if (result != null && !result.isEmpty()) {
            productsEntry = (List<Map<String, Object>>) result.get("items");
        }

        var products = productMapper.toProducts(productsEntry);
        return new Page<>(products, products.size(), limit, offset);
    }

    @Override
    public Product getProductBySku(String sku) {
        var url = apiBaseUrl + apiPath + PRODUCTS_ENDPOINT_PATH + "/" + sku;
        log.info("Loading product by SKU={}", sku);
        var response = (Map<String, Object>) restTemplate.getForObject(url, Map.class);
        return productMapper.toProduct(response);
    }
}

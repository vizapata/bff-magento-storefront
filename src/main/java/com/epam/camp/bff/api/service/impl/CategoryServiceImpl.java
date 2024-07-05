package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.mapper.CategoryMapper;
import com.epam.camp.bff.api.rest.dto.Category;
import com.epam.camp.bff.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORIES_ENDPOINT_PATH = "/V1/categories";
    private final RestTemplate restTemplate;
    private final CategoryMapper categoryMapper;

    @Value("${app.magento.api.baseUrl}")
    private String apiBaseUrl;
    @Value("${app.magento.api.path}")
    private String apiPath;

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> listCategories() {
        log.info("Listing categories from remote API");
        var response = (Map<String, Object>) restTemplate.getForObject(apiBaseUrl + apiPath + CATEGORIES_ENDPOINT_PATH, Map.class);
        return categoryMapper.mapToCategories(List.of(Optional.ofNullable(response).orElseThrow()), List.of());
    }
}

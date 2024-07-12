package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MagentoApiService implements ApiService {
    private final RestTemplate restTemplate;
    @Value("${app.magento.api.baseUrl}")
    private String apiBaseUrl;
    @Value("${app.magento.api.path}")
    private String apiPath;

    @Override
    public <T> T getForObject(String path, Class<T> responseType) {
        return restTemplate.getForObject(apiBaseUrl + apiPath + path, responseType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getForMap(String path) {
        return getForObject(path, Map.class);
    }

    @Override
    public <T> T postForObject(String path, Object body, Class<T> responseType) {
        return restTemplate.postForObject(apiBaseUrl + apiPath + path, body, responseType);
    }
}

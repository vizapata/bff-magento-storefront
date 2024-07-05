package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.service.AuthenticationService;
import com.epam.camp.bff.api.service.PropertiesService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertiesServiceImpl implements PropertiesService {
    private static final String COLORS_ENDPOINT_PATH = "/V1/products/attributes/color/options";
    private static final String SIZES_ENDPOINT_PATH = "/V1/products/attributes/size/options";
    private final AuthenticationService authenticationService;
    private Map<String, String> colors;
    private Map<String, String> sizes;
    @Value("${app.magento.api.baseUrl}")
    private String apiBaseUrl;
    @Value("${app.magento.api.path}")
    private String apiPath;
    private final RestTemplate restTemplate;

    @PostConstruct
    void init() {
        getColors();
    }

    @Override
    public Map<String, String> getColors() {
        if (colors == null) {
            colors = loadProperties(COLORS_ENDPOINT_PATH);
        }
        return colors;
    }

    @Override
    public Map<String, String> getSizes() {
        if (sizes == null) {
            sizes = loadProperties(SIZES_ENDPOINT_PATH);
        }
        return sizes;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> loadProperties(String endpoint) {
        var accessToken = authenticationService.getToken();
        var url = apiBaseUrl + apiPath + endpoint;
        var request = RequestEntity.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .build();
        var response = restTemplate.exchange(request, List.class);
        List<Map<String, Object>> responseBody = response.getBody();
        if (responseBody == null) {
            responseBody = List.of();
        }
        return responseBody.stream()
                .filter(map -> map.get("value") != null && !map.get("value").toString().isEmpty())
                .collect(Collectors.toMap(
                        map -> map.get("value").toString(),
                        map -> map.get("label").toString()));
    }

    @Override
    public String getColorByKey(String key) {
        return Optional.ofNullable(getColors())
                .orElseGet(Map::of)
                .get(key);
    }

    @Override
    public String getSizeByKey(String key) {
        return Optional.ofNullable(getSizes())
                .orElseGet(Map::of)
                .get(key);
    }
}

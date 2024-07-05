package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String AUTH_ENDPOINT_PATH = "/V1/integration/admin/token";

    @Value("${app.magento.api.baseUrl}")
    private String apiBaseUrl;
    @Value("${app.magento.api.path}")
    private String apiPath;
    @Value("${app.magento.api.user}")
    private String username;
    @Value("${app.magento.api.password}")
    private String password;

    private String accessToken;
    private final RestTemplate restTemplate;

    @Override
    public String login(String username, String password) {
        var requestBody = Map.of("username", username, "password", password);
        return restTemplate.postForObject(apiBaseUrl + apiPath + AUTH_ENDPOINT_PATH, requestBody, String.class);
    }

    @Override
    public String login() {
        return login(username, password);
    }

    @Override
    public synchronized String getToken() {
        if (accessToken == null) {
            accessToken = login();
        }
        return accessToken;
    }
}

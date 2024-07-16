package com.epam.camp.bff.api.service;

import java.util.Map;

public interface ApiService {

    <T> T getForObject(String path, Class<T> responseType);

    Map<String, Object> getForMap(String path);

    <T> T postForObject(String path, Object body, Class<T> responseType);

    default <T> T postForObject(String path, Class<T> responseType) {
        return postForObject(path, null, responseType);
    }

    <T> T putForObject(String path, Object body, Class<T> responseType);
}

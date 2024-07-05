package com.epam.camp.bff.api.service;

import java.util.Map;

public interface PropertiesService {
    Map<String, String> getColors();

    Map<String, String> getSizes();

    String getColorByKey(String key);

    String getSizeByKey(String key);
}

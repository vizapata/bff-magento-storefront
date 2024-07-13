package com.epam.camp.bff.api.mapper.impl;

import com.epam.camp.bff.api.mapper.ProductMapper;
import com.epam.camp.bff.api.rest.dto.Product;
import com.epam.camp.bff.api.rest.dto.Variant;
import com.epam.camp.bff.api.service.PropertiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {
    @Value("${app.magento.api.baseUrl}")
    private String apiBaseUrl;

    @Value("${app.magento.api.imagePath}")
    private String imagePath;

    private final PropertiesService propertiesService;

    @Override
    @SuppressWarnings("unchecked")
    public Product toProduct(Map<String, Object> data) {
        var customAttributes = (List<Map<String, String>>) data.get("custom_attributes");
        var slug = getAttribute(customAttributes, "attribute_code");
        var description = getAttribute(customAttributes, "description");
        var image = getAttribute(customAttributes, "image");
        var color = getAttribute(customAttributes, "color");
        var size = getAttribute(customAttributes, "size");
        var variant = new Variant()
                .withId(Integer.parseInt(data.get("id").toString()))
                .withSku(data.get("sku").toString())
                .withSlug(slug)
                .withName(data.get("name").toString())
                .withPrice(Double.parseDouble(data.get("price").toString()))
                .withAttribute("Color", color, propertiesService.getColorByKey(color))
                .withAttribute("Size", size, propertiesService.getSizeByKey(size))
                .withImage(apiBaseUrl + imagePath + image);
        return new Product(
                Integer.parseInt(data.get("id").toString()),
                data.get("name").toString(),
                description,
                slug,
                List.of(),
                variant,
                data.get("type_id").toString());
    }

    private static String getAttribute(List<Map<String, String>> customAttributes, String attributeName) {
        return customAttributes.stream()
                .filter(attribute -> attribute.get("attribute_code") != null)
                .filter(attribute -> attribute.get("attribute_code").equals(attributeName))
                .map(attribute -> attribute.get("value"))
                .findFirst()
                .orElse(null);
    }
}

package com.epam.camp.bff.api.mapper;

import com.epam.camp.bff.api.rest.dto.Ancestor;
import com.epam.camp.bff.api.rest.dto.Category;

import java.util.List;
import java.util.Map;

public interface CategoryMapper {
    List<Category> mapToCategories(List<Map<String, Object>> data, List<Ancestor> ancestors);
}

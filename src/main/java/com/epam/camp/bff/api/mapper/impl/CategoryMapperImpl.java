package com.epam.camp.bff.api.mapper.impl;

import com.epam.camp.bff.api.mapper.CategoryMapper;
import com.epam.camp.bff.api.rest.dto.Ancestor;
import com.epam.camp.bff.api.rest.dto.Category;
import com.epam.camp.bff.api.rest.dto.Parent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public List<Category> mapToCategories(List<Map<String, Object>> data, List<Ancestor> ancestors) {
        return data.stream()
                .flatMap(categoryData -> mapToCategory(categoryData, ancestors).stream())
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<Category> mapToCategory(Map<String, Object> data, List<Ancestor> ancestors) {
        Parent parent = null;
        if (!ancestors.isEmpty()) {
            parent = Optional.ofNullable(data.get("parent_id"))
                    .map(Object::toString)
                    .map(Integer::parseInt)
                    .map(Parent::new)
                    .orElse(null);
        }

        var currentCategory = new Category(
                Optional.ofNullable(data.get("id")).map(Object::toString).map(Integer::parseInt).orElse(null),
                Optional.ofNullable(data.get("name")).map(Object::toString).orElse(null),
                Optional.ofNullable(data.get("name")).map(Object::toString).orElse(null),
                Optional.ofNullable(data.get("id")).map(Object::toString).orElse(null),
                parent,
                ancestors);

        var categories = new ArrayList<Category>();
        categories.add(currentCategory);
        var children = (List<Map<String, Object>>) data.get("children_data");
        if (children != null && !children.isEmpty()) {
            var newAncestors = new ArrayList<>(ancestors);
            newAncestors.add(new Ancestor("category", currentCategory.id()));
            categories.addAll(mapToCategories(children, newAncestors));
        }
        return categories;
    }
}

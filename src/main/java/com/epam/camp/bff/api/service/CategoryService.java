package com.epam.camp.bff.api.service;

import com.epam.camp.bff.api.rest.dto.Category;

import java.util.List;

public interface CategoryService {
    List<Category> listCategories();
}

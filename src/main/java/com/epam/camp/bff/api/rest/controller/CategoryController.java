package com.epam.camp.bff.api.rest.controller;

import com.epam.camp.bff.api.rest.dto.Category;
import com.epam.camp.bff.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.storeFront.url}", maxAge = 1800L, methods = RequestMethod.GET)
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> listCategories() {
        return ResponseEntity.ok(categoryService.listCategories());
    }
}

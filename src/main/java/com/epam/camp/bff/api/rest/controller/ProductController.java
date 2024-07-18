package com.epam.camp.bff.api.rest.controller;

import com.epam.camp.bff.api.rest.dto.Page;
import com.epam.camp.bff.api.rest.dto.Product;
import com.epam.camp.bff.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.storeFront.url}", maxAge = 1800L, methods = RequestMethod.GET)
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> listProductsByCategory(@RequestParam Integer categoryId,
                                                                @RequestParam(defaultValue = "0", required = false) Integer offset,
                                                                @RequestParam(defaultValue = "20", required = false) Integer limit) {
        return ResponseEntity.ok(productService.listProductsByCategory(categoryId, offset, limit));
    }

    @GetMapping("{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }
}

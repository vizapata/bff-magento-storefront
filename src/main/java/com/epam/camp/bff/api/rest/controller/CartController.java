package com.epam.camp.bff.api.rest.controller;

import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.OrderResponse;
import com.epam.camp.bff.api.rest.dto.request.UpdateCartRequest;
import com.epam.camp.bff.api.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/carts")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.magento.storeFront.url}", maxAge = 1800L)
public class CartController {
    private final CartService cartService;

    @GetMapping("{id}")
    public ResponseEntity<Cart> getById(@PathVariable String id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Cart> createGuestCart() {
        return ResponseEntity.ok(cartService.createGuestCart());
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> addLineItem(@PathVariable String id, @RequestBody UpdateCartRequest lineItemRequest) {
        return ResponseEntity.ok(cartService.updateCartItem(id, lineItemRequest));
    }

    @PostMapping("{id}/order")
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable String id) {
        return ResponseEntity.ok(cartService.placeOrder(id));
    }
}

package com.epam.camp.bff.api.service;

import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.OrderResponse;
import com.epam.camp.bff.api.rest.dto.request.UpdateCartRequest;

public interface CartService {
    Cart findById(String id);

    Cart createGuestCart();

    Object updateCartItem(String id, UpdateCartRequest lineItemRequest);

    OrderResponse placeOrder(String id);
}

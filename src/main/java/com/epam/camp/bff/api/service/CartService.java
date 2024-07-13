package com.epam.camp.bff.api.service;

import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.CartItem;
import com.epam.camp.bff.api.rest.dto.request.AddLineItemRequest;

public interface CartService {
    Cart findById(String id);

    Cart createGuestCart();

    CartItem addLineItem(String id, AddLineItemRequest lineItemRequest);
}

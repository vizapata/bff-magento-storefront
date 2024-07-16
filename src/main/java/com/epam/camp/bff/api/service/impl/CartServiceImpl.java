package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.mapper.CartMapper;
import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.CartItem;
import com.epam.camp.bff.api.rest.dto.request.UpdateCartRequest;
import com.epam.camp.bff.api.service.ApiService;
import com.epam.camp.bff.api.service.CartService;
import com.epam.camp.bff.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CartServiceImpl implements CartService {
    private static final String CART_ENDPOINT_PATH = "V1/guest-carts";
    private final ApiService apiService;
    private final CartMapper cartMapper;
    private final ProductService productService;

    @Override
    public Cart findById(String id) {
        var response = apiService.getForMap(CART_ENDPOINT_PATH + "/" + id);
        return Optional.ofNullable(response)
                .map(data -> cartMapper.toCart(data, id))
                .orElseThrow();
    }

    @Override
    public Cart createGuestCart() {
        var cartId = apiService.postForObject(CART_ENDPOINT_PATH, String.class);
        return Optional.ofNullable(cartId)
                .map(id -> id.replace("\"", ""))
                .map(this::findById)
                .orElseThrow();
    }

    @Override
    public CartItem updateCartItem(String id, UpdateCartRequest updateCartRequest) {
        return switch (updateCartRequest.action()) {
            case AddLineItem -> new AddLineItemService().addLineItem(id, updateCartRequest);
            case ChangeLineItemQuantity -> new UpdateLineItemQuantityService().updateLineItemQuantity(id, updateCartRequest);
        };
    }


    private class AddLineItemService {
        public CartItem addLineItem(String id, UpdateCartRequest updateCartRequest) {
            var product = productService.getProductBySku(updateCartRequest.addLineItem().variantId());
            var cartItem = cartMapper.toCartItem(product, updateCartRequest.addLineItem().quantity());
            var request = Map.of("cartItem", cartItem);

            return apiService.postForObject(CART_ENDPOINT_PATH + "/" + id + "/items",
                    request,
                    CartItem.class);
        }
    }

    private class UpdateLineItemQuantityService {
        public CartItem updateLineItemQuantity(String id, UpdateCartRequest updateCartRequest) {
            var itemId = updateCartRequest.changeLineItemQuantity().lineItemId();
            var cart = findById(id);

            var productSku = cart.lineItems()
                    .stream()
                    .filter(lineItem -> lineItem.id().equals(updateCartRequest.changeLineItemQuantity().lineItemId()))
                    .map(cartLineItem -> cartLineItem.variant().sku())
                    .findFirst()
                    .orElseThrow();
            var product = productService.getProductBySku(productSku);
            var cartItem = cartMapper.toCartItem(product, updateCartRequest.changeLineItemQuantity().quantity());
            var request = Map.of("cartItem", cartItem);
            return apiService.putForObject(CART_ENDPOINT_PATH + "/" + id + "/items/" + itemId,
                    request,
                    CartItem.class);
        }
    }
}

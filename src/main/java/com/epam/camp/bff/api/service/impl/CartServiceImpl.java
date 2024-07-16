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

import java.util.List;
import java.util.Map;
import java.util.Optional;

interface CartActionService {
    Object update(String id, UpdateCartRequest updateCartRequest);
}

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
    public Object updateCartItem(String id, UpdateCartRequest updateCartRequest) {
        return getCartActionService(updateCartRequest).update(id, updateCartRequest);
    }

    private CartActionService getCartActionService(UpdateCartRequest updateCartRequest) {
        return switch (updateCartRequest.action()) {
            case AddLineItem -> new AddLineItemService();
            case ChangeLineItemQuantity -> new UpdateLineItemQuantityService();
            case RemoveLineItem -> new RemoveLineItemService();
            case SetShippingAddress -> new SetShippingAddressService();
        };
    }

    private class AddLineItemService implements CartActionService {

        @Override
        public CartItem update(String id, UpdateCartRequest updateCartRequest) {
            var product = productService.getProductBySku(updateCartRequest.addLineItem().variantId());
            var cartItem = cartMapper.toCartItem(product, updateCartRequest.addLineItem().quantity());
            var request = Map.of("cartItem", cartItem);

            return apiService.postForObject(CART_ENDPOINT_PATH + "/" + id + "/items",
                    request,
                    CartItem.class);
        }
    }

    private class UpdateLineItemQuantityService implements CartActionService {

        @Override
        public CartItem update(String id, UpdateCartRequest updateCartRequest) {
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

    private class RemoveLineItemService implements CartActionService {
        @Override
        public Boolean update(String id, UpdateCartRequest updateCartRequest) {
            var itemId = updateCartRequest.removeLineItem().lineItemId();
            return apiService.deleteForObject(CART_ENDPOINT_PATH + "/" + id + "/items/" + itemId,
                    Boolean.class);
        }
    }

    @SuppressWarnings("unchecked")
    private class SetShippingAddressService implements CartActionService {
        @Override
        public Map<String, Object> update(String id, UpdateCartRequest updateCartRequest) {
            var address = updateCartRequest.setShippingAddress();
            var shippingMethod = getShippingMethod(id);

            var billingAddress = Map.of(
                    "city", address.city(),
                    "country_id", address.country(),
                    "email", address.email(),
                    "firstname", address.firstName(),
                    "lastname", address.lastName(),
                    "postcode", address.postalCode(),
                    "region", address.region(),
                    "street", List.of(address.streetNumber(), address.streetName()));

            var addressInformation = Map.of(
                    "billing_address", billingAddress,
                    "custom_attributes", Map.of(),
                    "extension_attributes", Map.of(),
                    "shipping_address", billingAddress,
                    "shipping_carrier_code", shippingMethod.get("carrier_code"),
                    "shipping_method_code", shippingMethod.get("method_code"));

            var request = Map.of("addressInformation", addressInformation);

            return apiService.postForObject(CART_ENDPOINT_PATH + "/" + id + "/shipping-information",
                    request,
                    Map.class);
        }

        private Map<String, Object> getShippingMethod(String id) {
            List<Map<String, Object>> response = apiService.getForObject(CART_ENDPOINT_PATH + "/" + id + "/shipping-methods", List.class);
            return Optional.ofNullable(response)
                    .stream()
                    .filter(list -> !list.isEmpty())
                    .map(List::getFirst)
                    .findAny()
                    .orElseThrow();

        }
    }
}

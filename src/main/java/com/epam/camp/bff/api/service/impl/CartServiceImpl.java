package com.epam.camp.bff.api.service.impl;

import com.epam.camp.bff.api.mapper.CartMapper;
import com.epam.camp.bff.api.rest.dto.Cart;
import com.epam.camp.bff.api.rest.dto.CartItem;
import com.epam.camp.bff.api.rest.dto.OrderResponse;
import com.epam.camp.bff.api.rest.dto.request.UpdateCartRequest;
import com.epam.camp.bff.api.service.ApiService;
import com.epam.camp.bff.api.service.CartService;
import com.epam.camp.bff.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

interface CartActionService {
    Object update(String id, UpdateCartRequest updateCartRequest);
}

@Service
@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unchecked")
public class CartServiceImpl implements CartService {
    private static final String CART_ENDPOINT_PATH = "V1/guest-carts";
    private static final String COUNTRY_ENDPOINT_PATH = "V1/directory/countries/";
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

    @Override
    public OrderResponse placeOrder(String id) {
        var response = apiService.getForObject(CART_ENDPOINT_PATH + "/" + id + "/payment-methods", List.class);
        var paymentMethod = getItemFromListResponse(response);
        var request = Map.of("paymentMethod", Map.of(
                "method", paymentMethod.get("code")));
        var orderId = apiService.putForObject(CART_ENDPOINT_PATH + "/" + id + "/order", request, String.class);
        return new OrderResponse(orderId);
    }

    private CartActionService getCartActionService(UpdateCartRequest updateCartRequest) {
        return switch (updateCartRequest.action()) {
            case AddLineItem -> new AddLineItemService();
            case ChangeLineItemQuantity -> new UpdateLineItemQuantityService();
            case RemoveLineItem -> new RemoveLineItemService();
            case SetShippingAddress -> new SetShippingAddressService();
        };
    }

    private Map<String, Object> getItemFromListResponse(List<Map<String, Object>> response) {
        return Optional.ofNullable(response)
                .stream()
                .filter(list -> !list.isEmpty())
                .map(List::getFirst)
                .findAny()
                .orElseThrow();
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

    private class SetShippingAddressService implements CartActionService {

        @Override
        public Map<String, Object> update(String id, UpdateCartRequest updateCartRequest) {
            var address = updateCartRequest.setShippingAddress();
            var region = getRegionDetails(address.country(), address.region());

            var billingAddress = new HashMap<String, Object>();
            billingAddress.put("city", address.city());
            billingAddress.put("country_id", address.country());
            billingAddress.put("email", address.email());
            billingAddress.put("firstname", address.firstName());
            billingAddress.put("lastname", address.lastName());
            billingAddress.put("postcode", address.postalCode());
            billingAddress.put("region", region.get("name"));
            billingAddress.put("region_code", region.get("code"));
            billingAddress.put("region_id", region.get("id"));
            billingAddress.put("telephone", "555-5555555");
            billingAddress.put("street", List.of(address.streetNumber(), address.streetName()));
            billingAddress.put("same_as_billing", 1);

            var response = apiService.postForObject(CART_ENDPOINT_PATH + "/" + id + "/estimate-shipping-methods",
                    Map.of("address", billingAddress),
                    List.class);
            var shippingMethod = getItemFromListResponse(response);

            var addressInformation = Map.of(
                    "shipping_carrier_code", shippingMethod.get("carrier_code"),
                    "shipping_method_code", shippingMethod.get("method_code"),
                    "billing_address", billingAddress,
                    "custom_attributes", Map.of(),
                    "extension_attributes", Map.of(),
                    "shipping_address", billingAddress);

            var request = Map.of("addressInformation", addressInformation);

            return apiService.postForObject(CART_ENDPOINT_PATH + "/" + id + "/shipping-information",
                    request,
                    Map.class);
        }

        private Map<String, String> getRegionDetails(String country, String region) {
            return Optional.ofNullable(apiService.getForMap(COUNTRY_ENDPOINT_PATH + country))
                    .map(response -> response.get("available_regions"))
                    .filter(List.class::isInstance)
                    .map(item -> (List<Map<String, String>>) item)
                    .orElse(List.of())
                    .stream()
                    .filter(item -> region.equals(item.get("name")))
                    .findFirst()
                    .orElseThrow();
        }
    }

}

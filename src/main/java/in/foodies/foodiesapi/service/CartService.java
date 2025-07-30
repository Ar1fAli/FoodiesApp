package in.foodies.foodiesapi.service;

import in.foodies.foodiesapi.io.CartRequest;
import in.foodies.foodiesapi.io.CartResponse;

public interface CartService {

    CartResponse addToCart(CartRequest cartRequest);

    CartResponse getCart();

    void clearCart();

    CartResponse removeFromCart(CartRequest cartRequest);
}

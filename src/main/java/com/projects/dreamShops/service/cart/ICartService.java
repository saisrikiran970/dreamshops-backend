package com.projects.dreamShops.service.cart;

import com.projects.dreamShops.model.Cart;
import com.projects.dreamShops.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void deleteCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}

package com.projects.dreamShops.service.cart;
import com.projects.dreamShops.exceptions.ResourceNotFoundException;
import com.projects.dreamShops.model.Cart;
import com.projects.dreamShops.model.User;
import com.projects.dreamShops.repository.CartItemRepository;
import com.projects.dreamShops.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart getCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cart not found..!"));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cart;
    }

    @Transactional
    @Override
    public void deleteCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getItems().clear();
        cartRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCart(id);
        return cart.getTotalAmount();
    }

    @Override
    public Cart initializeNewCart(User user){
       return Optional.ofNullable(getCartByUserId(user.getId()))
               .orElseGet(()->{
                   Cart cart = new Cart();
                   cart.setUser(user);
                   return cartRepository.save(cart);
               });
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

//    @Override
//    public Cart getCartByUserId(Long userId) {
//        Cart cart = cartRepository.findByUserId(userId);
//        if (cart == null) {
//            throw new ResourceNotFoundException("Cart not found for user " + userId);
//        }
//        return cart;
//    }

}

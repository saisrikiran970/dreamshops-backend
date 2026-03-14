package com.projects.dreamShops.service.cartItem;

import com.projects.dreamShops.exceptions.ResourceNotFoundException;
import com.projects.dreamShops.model.Cart;
import com.projects.dreamShops.model.CartItem;
import com.projects.dreamShops.model.Product;
import com.projects.dreamShops.repository.CartItemRepository;
import com.projects.dreamShops.repository.CartRepository;
import com.projects.dreamShops.service.cart.ICartService;
import com.projects.dreamShops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService implements ICartItemService{

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;
    private final ICartService cartService;

    @Override
    @Transactional
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        //1. Get the cart
        //2. Get the product
        //3. Check if the product already in the cart
        //4. If yes, then increase the quantity with requested quantity
        //5. If no, then initiate the new CartItem entry
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);

        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());
        if(cartItem.getId() == null){
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

//        Cart cart = cartRepository.findById(cartId)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
//
//        Product product = productService.getProductById(productId);
//
//        CartItem cartItem = cart.getItems()
//                .stream()
//                .filter(item -> item.getProduct().getId().equals(productId))
//                .findFirst()
//                .orElseGet(() -> {
//                    CartItem newItem = new CartItem();
//                    newItem.setCart(cart);
//                    newItem.setProduct(product);
//                    newItem.setUnitPrice(product.getPrice());
//                    cart.addItem(newItem);
//                    return newItem;
//                });
//
//        cartItem.setQuantity(cartItem.getQuantity() + quantity);
//        cartItem.setTotalPrice();
//
//        // Only save the parent — cascade will handle items
//        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem cartItem = getCartItem(cartId,productId);
        cart.removeItem(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });
        BigDecimal totalAmount = cart.getItems()
                .stream()
                .map(CartItem ::getTotalPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId){
        Cart cart = cartService.getCart(cartId);
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("Item not found"));
    }
}

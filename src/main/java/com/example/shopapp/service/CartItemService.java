package com.example.shopapp.service;

import com.example.shopapp.entity.CartItem;
import com.example.shopapp.entity.Product;
import com.example.shopapp.entity.User;
import com.example.shopapp.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    public List<CartItem> findByUser(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void addProductToCart(User user, Product product, int quantityToAdd) {
        Optional<CartItem> existing = cartItemRepository.findByUserAndProduct_Id(user, product.getId());

        int currentQuantity = existing.map(CartItem::getQuantity).orElse(0);
        int available = product.getQuantity();

        if (currentQuantity + quantityToAdd > available) {
            throw new IllegalArgumentException("Nie można dodać więcej niż dostępna ilość produktu: " + product.getName());
        }

        CartItem item = existing.orElseGet(() -> {
            CartItem ci = new CartItem();
            ci.setUser(user);
            ci.setProduct(product);
            return ci;
        });

        item.setQuantity(currentQuantity + quantityToAdd);
        cartItemRepository.save(item);
    }

    public void removeItem(User user, Long productId) {
        cartItemRepository.findByUserAndProduct_Id(user, productId)
                .ifPresent(cartItemRepository::delete);
    }

    public void updateQuantity(User user, Long productId, int quantity) {
        Optional<CartItem> item = cartItemRepository.findByUserAndProduct_Id(user, productId);
        item.ifPresent(ci -> {
            ci.setQuantity(quantity);
            cartItemRepository.save(ci);
        });
    }
}

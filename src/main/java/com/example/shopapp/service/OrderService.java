package com.example.shopapp.service;

import com.example.shopapp.entity.*;
import com.example.shopapp.exception.OrderNotFoundException;
import com.example.shopapp.repository.CartItemRepository;
import com.example.shopapp.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    private final ProductService productService;

    public Page<Order> findAllPaged(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> findOrdersWithItemsByUserPaged(User user, Pageable pageable) {
        return orderRepository.findAllWithItemsAndProductsByUser(user, pageable);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Zamówienie o ID " + id + " nie istnieje"));
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public Order findByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Zamówienie o ID " + id + " nie istnieje"));
    }

    public void deleteById(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    @Transactional
    public void createOrderFromCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Koszyk jest pusty");
        }

        if (user.getAddress() == null ||
                user.getAddress().getFullName() == null ||
                user.getAddress().getStreet() == null ||
                user.getAddress().getCity() == null ||
                user.getAddress().getPostalCode() == null ||
                user.getAddress().getPhone() == null) {

            throw new IllegalStateException("Aby złożyć zamówienie, musisz uzupełnić dane adresowe.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productService.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException("Nie znaleziono produktu w bazie"));
            int quantity = cartItem.getQuantity();

            if (product.getQuantity() < quantity) {
                throw new IllegalStateException("Brak wystarczającej ilości produktu: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - quantity);

            if (product.getQuantity() == 0) {
                productService.delete(product.getId());
            } else {
                productService.save(product);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);
    }
}

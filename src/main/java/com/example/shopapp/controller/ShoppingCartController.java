package com.example.shopapp.controller;

import com.example.shopapp.entity.*;
import com.example.shopapp.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class ShoppingCartController {

    private final ProductService productService;
    private final UserService userService;
    private final CartItemService cartItemService;
    private final OrderService orderService;

    @GetMapping
    public String viewCart(Model model, Principal principal, HttpSession session) {
        List<CartItem> items;

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            items = cartItemService.findByUser(user);
        } else {
            items = (List<CartItem>) session.getAttribute("cart");
            if (items == null) items = new ArrayList<>();
        }

        model.addAttribute("cartItems", items);
        model.addAttribute("total", items.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum());
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
                            Principal principal, HttpSession session) {

        Product product = productService.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            cartItemService.addProductToCart(user, product, quantity);
        } else {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) cart = new ArrayList<>();
            Optional<CartItem> existing = cart.stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst();

            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + quantity);
            } else {
                CartItem item = new CartItem();
                item.setQuantity(quantity);
                item.setProduct(product);
                cart.add(item);
            }
            session.setAttribute("cart", cart);
        }

        return "redirect:/cart";
    }

    @PostMapping("/merge")
    public void mergeSessionCartToDatabase(HttpSession session, Principal principal) {
        if (principal == null) return;

        List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
        if (sessionCart == null || sessionCart.isEmpty()) return;

        User user = userService.findByUsername(principal.getName());
        for (CartItem item : sessionCart) {
            cartItemService.addProductToCart(user, item.getProduct(), item.getQuantity());
        }
        session.removeAttribute("cart");
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 Principal principal, HttpSession session) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            cartItemService.updateQuantity(user, productId, quantity);
        } else {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                for (CartItem item : cart) {
                    if (item.getProduct().getId().equals(productId)) {
                        item.setQuantity(quantity);
                        break;
                    }
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeCartItem(@RequestParam Long productId,
                                 Principal principal, HttpSession session) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            cartItemService.removeItem(user, productId);
        } else {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                cart.removeIf(item -> item.getProduct().getId().equals(productId));
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/summary")
    public String summary(Model model, Principal principal, HttpSession session) {
        User user = userService.findByUsername(principal.getName());
        List<CartItem> items;
        if (principal != null) {
            items = cartItemService.findByUser(user);
        } else {
            items = (List<CartItem>) session.getAttribute("cart");
            if (items == null) items = new ArrayList<>();
        }

        double total = items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
        }

        model.addAttribute("address", address);
        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);
        return "summary";
    }

    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("address") Address address,
                           Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (user.getAddress() == null) {
            address.setUser(user);
            user.setAddress(address);
        } else {
            Address current = user.getAddress();
            current.setFullName(address.getFullName());
            current.setPhone(address.getPhone());
            current.setStreet(address.getStreet());
            current.setCity(address.getCity());
            current.setPostalCode(address.getPostalCode());
        }

        userService.save(user);

        orderService.createOrderFromCart(user);

        return "redirect:/thank-you";
    }




}

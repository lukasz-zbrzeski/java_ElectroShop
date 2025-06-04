package com.example.shopapp.controller;

import com.example.shopapp.entity.Address;
import com.example.shopapp.entity.OrderStatus;
import com.example.shopapp.entity.User;
import com.example.shopapp.entity.Order;
import com.example.shopapp.service.OrderService;
import com.example.shopapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String profilePage(@RequestParam(defaultValue = "0") int page, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String currentUsername = authentication.getName();
        User user = userService.findByUsername(currentUsername);
        if (user.getAddress() == null) {
            user.setAddress(new Address());
        }

        int pageSize = 10;
        Page<Order> orderPage = orderService.findOrdersWithItemsByUserPaged(user, PageRequest.of(page, pageSize, Sort.by("id").ascending()));



        Map<OrderStatus, String> statusTranslation = Map.of(
                OrderStatus.PENDING, "Oczekujące",
                OrderStatus.PROCESSING, "W trakcie realizacji",
                OrderStatus.COMPLETED, "Zrealizowane",
                OrderStatus.CANCELLED, "Anulowane"
        );

        model.addAttribute("statusTranslation", statusTranslation);
        model.addAttribute("user", user);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("orderCurrentPage", page);
        model.addAttribute("orderTotalPages", orderPage.getTotalPages());

        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @RequestParam(required = false) String newPassword,
                                Principal principal) {

        User currentUser = userService.findByUsername(principal.getName());

        currentUser.setUsername(updatedUser.getUsername());
        currentUser.setEmail(updatedUser.getEmail());

        Address updatedAddress = updatedUser.getAddress();
        if (updatedAddress != null) {
            if (currentUser.getAddress() == null) {
                updatedAddress.setUser(currentUser);
                currentUser.setAddress(updatedAddress);
            } else {
                Address currentAddress = currentUser.getAddress();
                currentAddress.setFullName(updatedAddress.getFullName());
                currentAddress.setPhone(updatedAddress.getPhone());
                currentAddress.setStreet(updatedAddress.getStreet());
                currentAddress.setCity(updatedAddress.getCity());
                currentAddress.setPostalCode(updatedAddress.getPostalCode());
            }
        }

        if (newPassword != null && !newPassword.isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }

        userService.save(currentUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                currentUser.getUsername(),
                currentUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + currentUser.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/profile?success";
    }


}

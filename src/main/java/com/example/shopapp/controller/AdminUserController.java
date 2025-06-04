package com.example.shopapp.controller;

import com.example.shopapp.entity.Role;
import com.example.shopapp.entity.User;
import com.example.shopapp.repository.UserRepository;
import com.example.shopapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long userId,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        User currentUser = userService.findByUsername(userDetails.getUsername());

        if (!Objects.equals(currentUser.getId(), userId)) {
            userRepository.deleteById(userId);
        }

        return "redirect:/admin-panel";
    }

    @PostMapping("/role")
    public String changeUserRole(@RequestParam Long userId, @RequestParam Role role) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
        return "redirect:/admin-panel?roleChanged";
    }
}
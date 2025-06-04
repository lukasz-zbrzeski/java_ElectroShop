package com.example.shopapp.controller;

import com.example.shopapp.entity.Order;
import com.example.shopapp.entity.OrderStatus;
import com.example.shopapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @PostMapping("/update")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam OrderStatus status) {
        Order order = orderService.findById(orderId);

        if (order != null) {
            order.setStatus(status);
            orderService.save(order);
        }

        return "redirect:/admin-panel";
    }

    @PostMapping("/delete")
    public String deleteOrder(@RequestParam Long orderId) {
        if (orderService.existsById(orderId)) {
            orderService.deleteById(orderId);
        }
        return "redirect:/admin-panel";
    }
}
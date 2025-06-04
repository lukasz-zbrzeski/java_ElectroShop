package com.example.shopapp.controller;

import com.example.shopapp.entity.Order;
import com.example.shopapp.entity.OrderStatus;
import com.example.shopapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderDetailsController {

    private final OrderService orderService;

    @GetMapping("/details/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.findByIdWithItems(id);

        Map<OrderStatus, String> statusTranslation = Map.of(
                OrderStatus.PENDING, "Oczekujące",
                OrderStatus.PROCESSING, "W trakcie realizacji",
                OrderStatus.COMPLETED, "Zrealizowane",
                OrderStatus.CANCELLED, "Anulowane"
        );
        model.addAttribute("statusTranslation", statusTranslation);

        model.addAttribute("order", order);
        return "order-details";
    }

}

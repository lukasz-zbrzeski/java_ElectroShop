package com.example.shopapp.controller;

import com.example.shopapp.entity.OrderStatus;
import com.example.shopapp.entity.Product;
import com.example.shopapp.service.OrderService;
import com.example.shopapp.service.ProductService;
import com.example.shopapp.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/admin-panel")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPageController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    public AdminPageController(UserService userService, OrderService orderService, ProductService productService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    private static final Map<OrderStatus, String> statusTranslation = Map.of(
            OrderStatus.PENDING, "Oczekujące",
            OrderStatus.PROCESSING, "W realizacji",
            OrderStatus.COMPLETED, "Zakończone",
            OrderStatus.CANCELLED, "Anulowane"
    );

    @GetMapping
    public String showAdminPanel(Model model,
                                 @RequestParam(defaultValue = "0") int userPage,
                                 @RequestParam(defaultValue = "0") int orderPage,
                                 @RequestParam(defaultValue = "0") int productPage) {

        int pageSize = 10;

        var usersPage = userService.findAllPaged(PageRequest.of(userPage, pageSize, Sort.by("id").ascending()));
        var ordersPage = orderService.findAllPaged(PageRequest.of(orderPage, pageSize, Sort.by("id").ascending()));
        var productsPage = productService.findAllPaged(PageRequest.of(productPage, pageSize, Sort.by("id").ascending()));

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("userCurrentPage", userPage);
        model.addAttribute("userTotalPages", usersPage.getTotalPages());

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("orderCurrentPage", orderPage);
        model.addAttribute("orderTotalPages", ordersPage.getTotalPages());

        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("productCurrentPage", productPage);
        model.addAttribute("productTotalPages", productsPage.getTotalPages());

        model.addAttribute("statusTranslation", statusTranslation);

        return "admin-panel";
    }

}
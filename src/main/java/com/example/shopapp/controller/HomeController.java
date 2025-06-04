package com.example.shopapp.controller;

import com.example.shopapp.entity.Product;
import com.example.shopapp.repository.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String home(Model model,
                       Authentication authentication,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String category) {

        String username = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "Gość";

        List<Product> products;
        if ((name != null && !name.isBlank()) || (category != null && !category.isBlank())) {
            products = productRepository.searchByNameAndCategory(
                    name != null ? name : "", category != null ? category : "");
        } else {
            products = productRepository.findAllByActiveTrue();
        }

        List<String> categories = productRepository.findDistinctCategoryNames();

        model.addAttribute("username", username);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchName", name);
        return "index";
    }
}

package com.example.shopapp.controller;

import com.example.shopapp.entity.Product;
import com.example.shopapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductDetailsController {

    private final ProductRepository productRepository;

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu o ID: " + id));

        model.addAttribute("product", product);
        return "product";
    }
}

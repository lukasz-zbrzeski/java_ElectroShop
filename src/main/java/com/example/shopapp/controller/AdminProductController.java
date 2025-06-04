package com.example.shopapp.controller;

import com.example.shopapp.entity.Category;
import com.example.shopapp.entity.Product;
import com.example.shopapp.service.CategoryService;
import com.example.shopapp.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam double price,
                             @RequestParam String category,
                             @RequestParam String description,
                             @RequestParam int quantity,
                             @RequestParam(required = false) String imageUrl) {

        Category existingCategory = categoryService.findByName(category);
        if (existingCategory == null) {
            existingCategory = new Category();
            existingCategory.setName(category);
            categoryService.save(existingCategory);
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setImageUrl(imageUrl);
        product.setCategory(existingCategory);

        productService.save(product);
        return "redirect:/admin-panel";
    }

    @PostMapping("/increase")
    public String increaseQuantity(@RequestParam Long productId) {
        productService.increaseQuantity(productId);
        return "redirect:/admin-panel";
    }

    @PostMapping("/decrease")
    public String decreaseQuantity(@RequestParam Long productId) {
        productService.decreaseQuantity(productId);
        return "redirect:/admin-panel";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("productId") Long productId,
                                @RequestParam(required = false) Integer productPage,
                                @RequestParam(required = false) Integer userPage,
                                @RequestParam(required = false) Integer orderPage) {
        productService.delete(productId);

        return "redirect:/admin-panel"
                + "?productPage=" + (productPage != null ? productPage : 0)
                + "&userPage=" + (userPage != null ? userPage : 0)
                + "&orderPage=" + (orderPage != null ? orderPage : 0);
    }

}

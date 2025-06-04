package com.example.shopapp.service;

import com.example.shopapp.entity.Product;
import com.example.shopapp.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findAllPaged(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable);
    }
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void increaseQuantity(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        product.setQuantity(product.getQuantity() + 1);
        productRepository.save(product);
    }

    public void decreaseQuantity(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getQuantity() > 0) {
            product.setQuantity(product.getQuantity() - 1);
            productRepository.save(product);
        }
    }

    public void delete(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setActive(false);
            productRepository.save(product);
        } else {
            throw new EntityNotFoundException("Produkt o ID " + id + " nie istnieje.");
        }
    }

}

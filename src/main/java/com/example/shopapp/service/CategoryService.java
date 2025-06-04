package com.example.shopapp.service;

import com.example.shopapp.entity.Category;
import com.example.shopapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name).orElse(null);
    }

    public void save(Category category) {
        categoryRepository.save(category);
    }

}

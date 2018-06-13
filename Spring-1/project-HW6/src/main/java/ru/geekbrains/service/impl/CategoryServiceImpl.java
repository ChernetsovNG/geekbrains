package ru.geekbrains.service.impl;

import org.springframework.stereotype.Service;
import ru.geekbrains.entity.Category;
import ru.geekbrains.repository.CategoryRepository;
import ru.geekbrains.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

}

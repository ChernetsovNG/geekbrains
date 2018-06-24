package ru.geekbrains.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public Category get(String id) {
        return categoryRepository.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void remove(Category category) {
        categoryRepository.delete(category);
    }

}

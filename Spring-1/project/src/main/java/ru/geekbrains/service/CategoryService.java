package ru.geekbrains.service;

import ru.geekbrains.entity.Category;

import java.util.List;

public interface CategoryService {
    Category get(String id);

    List<Category> getAll();

    void save(Category category);

    void remove(Category category);
}

package ru.geekbrains.service;

import ru.geekbrains.entity.Category;

import java.util.List;

public interface CategoryService {
    /**
     * Добавить новую категорию
     *
     * @param category - категория
     */
    void addCategory(Category category);

    /**
     * Получение всех категорий
     *
     * @return - список категорий
     */
    List<Category> getAll();

    Category get(String id);
}

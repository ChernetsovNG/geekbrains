package ru.nchernetsov.controller;

import ru.nchernetsov.dao.CategoryDAO;
import ru.nchernetsov.entity.Category;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;

@Named(value = "categoryController")
@ApplicationScoped
public class CategoryController {

    @Inject
    private CategoryDAO categoryDAO;

    public Collection<Category> getCategories() {
        return new ArrayList<>(categoryDAO.getCategories());
    }

    public void addCategory(String categoryName) {
        categoryDAO.persist(new Category(categoryName));
    }

    public void removeCategory(Category category) {
        categoryDAO.removeCategory(category.getId());
    }

    public Category getCategoryByName(String categoryName) {
        return categoryDAO.getCategoryByName(categoryName);
    }

}

package ru.nchernetsov.controller;

import ru.nchernetsov.dao.CategoryDAO;
import ru.nchernetsov.entity.Category;
import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Collection;

@Named(value = "categoryController")
@ApplicationScoped
@Interceptors({LoggerInterceptor.class})
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

    Category getCategoryByName(String categoryName) {
        return categoryDAO.getCategoryByName(categoryName);
    }
}

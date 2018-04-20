package ru.nchernetsov.controller.cdi;

import ru.nchernetsov.dao.cdi.CategoryDAO;
import ru.nchernetsov.entity.cdi.Category;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Named(value = "categoryControllerCDI")
@ApplicationScoped
public class CategoryController {

    @Inject
    private CategoryDAO categoryDAO;

    public Collection<Category> getCategories() {
        return new ArrayList<>(categoryDAO.getCategories());
    }

    public void addCategory(String categoryName) {
        categoryDAO.addCategory(new Category(categoryName));
    }

    public void removeCategory(Category category) {
        categoryDAO.removeCategory(category);
    }

    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryDAO.getCategoryByName(categoryName);
    }

}
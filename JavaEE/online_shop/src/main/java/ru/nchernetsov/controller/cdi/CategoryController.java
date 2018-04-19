package ru.nchernetsov.controller.cdi;

import ru.nchernetsov.dao.cdi.CategoryDAO;
import ru.nchernetsov.entity.cdi.Category;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Named(value = "categoryControllerCDI")
@ViewScoped
public class CategoryController implements Serializable {

    @Inject
    private CategoryDAO categoryDAO;

    public Collection<Category> getCategories() {
        return new HashSet<>(categoryDAO.getCategories());
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

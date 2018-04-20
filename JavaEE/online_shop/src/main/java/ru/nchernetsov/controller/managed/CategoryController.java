package ru.nchernetsov.controller.managed;

import ru.nchernetsov.dao.managed.CategoryDAO;
import ru.nchernetsov.entity.managed.Category;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@ManagedBean(name = "categoryControllerManaged")
@ApplicationScoped
public class CategoryController implements Serializable {

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

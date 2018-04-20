package ru.nchernetsov.controller.managed;

import ru.nchernetsov.controller.AbstractController;
import ru.nchernetsov.dao.managed.CategoryDAO;
import ru.nchernetsov.entity.managed.Category;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Optional;

@ManagedBean(name = "categoryEditControllerManaged")
@ViewScoped
public class CategoryEditController extends AbstractController implements Serializable {

    private final String id = getParamString("id");

    @Inject
    private CategoryDAO categoryDAO;

    private Category category = null;

    @PostConstruct
    private void init() {
        Optional<Category> categoryOptional = categoryDAO.getCategoryById(id);
        category = categoryOptional.orElse(null);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String save() {
        categoryDAO.addCategory(category);
        return "/faces/managed/categories";
    }
}

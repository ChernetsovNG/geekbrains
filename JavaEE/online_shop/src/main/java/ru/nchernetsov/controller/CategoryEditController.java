package ru.nchernetsov.controller;

import ru.nchernetsov.dao.CategoryDAO;
import ru.nchernetsov.entity.Category;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named(value = "categoryEditController")
@ViewScoped
public class CategoryEditController extends AbstractController implements Serializable {

    private final String id = getParamString("id");

    @Inject
    private CategoryDAO categoryDAO;

    private Category category = null;

    @PostConstruct
    private void init() {
        category = categoryDAO.getCategoryById(id);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String save() {
        categoryDAO.merge(category);
        return "/faces/categories";
    }
}

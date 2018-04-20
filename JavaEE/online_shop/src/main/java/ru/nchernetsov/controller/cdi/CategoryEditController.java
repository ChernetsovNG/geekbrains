package ru.nchernetsov.controller.cdi;

import ru.nchernetsov.controller.AbstractController;
import ru.nchernetsov.dao.cdi.CategoryDAO;
import ru.nchernetsov.entity.cdi.Category;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Optional;

@Named(value = "categoryEditControllerCDI")
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
        return "/faces/cdi/categories";
    }
}

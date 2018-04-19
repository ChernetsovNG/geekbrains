package ru.nchernetsov.beans.managed;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "categoryList", eager = true)
@SessionScoped
public class CategoryList implements Serializable {
    private final List<Category> categories = new ArrayList<>();

    {
        categories.add(new Category("books"));
        categories.add(new Category("electronics"));
    }

    public CategoryList() {
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }
}

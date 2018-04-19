package ru.nchernetsov.beans.cdi;

import ru.nchernetsov.beans.managed.Category;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named(value = "categoryListCDI")
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

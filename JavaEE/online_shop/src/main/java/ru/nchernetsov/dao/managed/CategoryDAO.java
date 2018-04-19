package ru.nchernetsov.dao.managed;

import ru.nchernetsov.entity.managed.Category;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ManagedBean(name = "categoryDAOManaged", eager = true)
@ApplicationScoped
public class CategoryDAO implements Serializable {
    private final List<Category> categories = new ArrayList<>();

    {
        categories.add(new Category("books"));
        categories.add(new Category("electronics"));
    }

    public CategoryDAO() {
    }

    public Collection<Category> getCategories() {
        return categories;
    }

    public Optional<Category> getCategoryByName(String categoryName) {
        return categories.stream()
            .filter(category -> category.getName().equals(categoryName))
            .findFirst();
    }

    public Optional<Category> getCategoryById(String id) {
        return categories.stream()
            .filter(category -> category.getId().toString().equals(id))
            .findFirst();
    }

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }
}

package ru.nchernetsov.beans.managed;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.UUID;

/**
 * Категория
 */
@ManagedBean(name = "category", eager = true)
@SessionScoped
public class Category {
    /**
     * Идентификатор
     */
    private UUID id;
    /**
     * Название
     */
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        return getName() != null ? getName().equals(category.getName()) : category.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Category{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}

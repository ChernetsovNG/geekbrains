package ru.nchernetsov.beans.cdi;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.UUID;

@Named(value = "categoryCDI")
@SessionScoped
public class Category implements Serializable {
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
        if (!(o instanceof ru.nchernetsov.beans.managed.Category)) return false;

        ru.nchernetsov.beans.managed.Category category = (ru.nchernetsov.beans.managed.Category) o;

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

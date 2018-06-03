package ru.geekbrains.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category extends AbstractEntity {
    @ManyToMany
    private Set<Advertisement> advertisements = new HashSet<>();

    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    void addAdvertisement(Advertisement advertisement) {
        advertisements.add(advertisement);
    }

    public void removeAdvertisement(Advertisement advertisement) {
        advertisements.remove(advertisement);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", name='" + name + '\'' +
            "}";
    }
}

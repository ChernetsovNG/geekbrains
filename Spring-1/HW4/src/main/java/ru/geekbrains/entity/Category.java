package ru.geekbrains.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category extends AbstractEntity {
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private final List<Advertisement> advertisements = new ArrayList<>();

    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    void addAdvertisement(Advertisement advertisement) {
        getAdvertisements().add(advertisement);
    }

    public void removeAdvertisement(Advertisement advertisement) {
        getAdvertisements().remove(advertisement);
    }

    public List<Advertisement> getAdvertisements() {
        return advertisements;
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

package ru.geekbrains.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends AbstractEntity {
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private final List<Advertisement> advertisements = new ArrayList<>();

    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public List<Advertisement> getAdvertisements() {
        return advertisements;
    }

    public void addAdvertisement(Advertisement advertisement) {
        advertisements.add(advertisement);
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

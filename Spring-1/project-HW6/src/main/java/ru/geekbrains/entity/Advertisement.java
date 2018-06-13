package ru.geekbrains.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "advertisement")
public class Advertisement extends AbstractEntity {
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private final List<Category> categories = new ArrayList<>();

    private String name;

    private String text;

    private String phone;

    public Advertisement() {
    }

    public Advertisement(String name, String text, String phone) {
        this.name = name;
        this.text = text;
        this.phone = phone;
    }

    public void addCategory(Category category) {
        getCategories().add(category);
        category.addAdvertisement(this);
    }

    public void removeCategory(Category category) {
        category.getAdvertisements().remove(this);
        getCategories().remove(category);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "categories=" + categories +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

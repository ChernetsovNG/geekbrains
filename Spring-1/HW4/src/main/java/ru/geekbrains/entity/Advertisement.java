package ru.geekbrains.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "advertisement")
public class Advertisement extends AbstractEntity {
    @OneToMany
    private List<Category> categories = new ArrayList<>();

    private String name;

    private String text;

    private String phone;

    public Advertisement() {
    }

    public Advertisement(List<Category> categories, String name, String text, String phone) {
        this.categories = categories;
        this.name = name;
        this.text = text;
        this.phone = phone;
        for (Category category : categories) {
            category.addAdvertisement(this);
        }
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", phone='" + phone + '\'' +
                "}";
    }
}

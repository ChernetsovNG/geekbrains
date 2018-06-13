package ru.geekbrains.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends AbstractEntity {
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private final List<Article> articles = new ArrayList<>();

    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    void addArticle(Article article) {
        getArticles().add(article);
    }

    public void removeArticle(Article article) {
        getArticles().remove(article);
    }

    public List<Article> getArticles() {
        return articles;
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

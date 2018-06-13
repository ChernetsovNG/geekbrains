package ru.geekbrains.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Article extends AbstractEntity {
    @ManyToOne
    private Category category;

    private String title;

    private String text;

    @OneToOne
    private Author author;

    public Article() {
    }

    public Article(Category category, String title, String text, Author author) {
        this.category = category;
        this.title = title;
        this.text = text;
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Article{" +
                "category=" + category +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}

package ru.geekbrains.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Article extends AbstractEntity {
    @ManyToOne
    private Category category;

    private String title;

    private String content;

    @OneToOne
    private Author author;

    @Column(name="published_date", insertable=false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date publishedDate;

    public Article() {
    }

    public Article(Category category, String title, String content, Author author) {
        this.category = category;
        this.title = title;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String text) {
        this.content = text;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return "Article{" +
                "category=" + category +
                ", title='" + title + '\'' +
                ", text='" + content + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}

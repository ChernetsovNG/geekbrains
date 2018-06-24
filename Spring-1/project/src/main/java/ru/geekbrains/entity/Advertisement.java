package ru.geekbrains.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Advertisement extends AbstractEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @Size(min = 2, max = 150, message = "{validation.title.size}")
    private String title;

    @Size(min = 10, message = "{validation.content.min}")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @Valid
    private Company company;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishedDate;

    public Advertisement() {
        publishedDate = new Date();
    }

    public Advertisement(Category category, String title, String content, Company company) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.company = company;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        category.addAdvertisement(this);
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
        company.addAdvertisement(this);
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
            "category=" + category +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", company=" + company +
            ", publishedDate=" + publishedDate +
            '}';
    }
}

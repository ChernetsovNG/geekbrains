package ru.geekbrains.controller;

import ru.geekbrains.entity.Article;

import java.util.List;

public class ArticlesAjax {

    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }

    void setArticles(List<Article> articles) {
        this.articles = articles;
    }

}

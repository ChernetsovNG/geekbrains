package ru.geekbrains.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.geekbrains.entity.Article;

public interface ArticleService {

    Page<Article> getAll(Pageable pageable);

    Article get(String id);

    void save(Article article);

    Page<Article> getByCategoryId(String id, Pageable pageable);
}

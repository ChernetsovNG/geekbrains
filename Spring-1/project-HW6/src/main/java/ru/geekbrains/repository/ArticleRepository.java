package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    Article getById(String id);
}

package ru.geekbrains.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    Article getById(String id);

    @Query("SELECT a FROM Article a WHERE a.category.id=:id")
    Page<Article> findByCategoryId(@Param("id") String id, Pageable pageable);
}

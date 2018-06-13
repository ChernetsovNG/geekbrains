package ru.geekbrains.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Article;
import ru.geekbrains.repository.ArticleRepository;
import ru.geekbrains.service.ArticleService;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> getAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Article get(String id) {
        return articleRepository.getById(id);
    }

    @Override
    public void save(Article article) {
        articleRepository.save(article);
    }
}

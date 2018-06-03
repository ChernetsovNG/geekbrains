package ru.geekbrains.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Category;

import java.util.List;

@Component
@Transactional
public class CategoryDAO extends AbstractDAO {

    public List<Category> getCategories() {
        return em.createQuery("SELECT c FROM Category c", Category.class)
            .getResultList();
    }

    public Category merge(Category category) {
        return em.merge(category);
    }
}

package ru.nchernetsov.dao;

import ru.nchernetsov.entity.Category;
import ru.nchernetsov.interceptor.LoggerInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Stateless
@Interceptors({LoggerInterceptor.class})
public class CategoryDAO extends AbstractDAO {

    public Collection<Category> getCategories() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    public Category getCategoryByName(String categoryName) {
        if (categoryName == null) {
            return null;
        }
        List namesList = em.createQuery("SELECT c FROM Category c WHERE c.name LIKE :name")
            .setParameter("name", categoryName).getResultList();
        return (Category) namesList.get(0);
    }

    public Category getCategoryById(String id) {
        if (id == null) {
            return null;
        }
        return em.find(Category.class, UUID.fromString(id));
    }

    public void persist(Category category) {
        if (category == null) {
            return;
        }
        em.persist(category);
    }

    public void merge(Category category) {
        if (category == null) {
            return;
        }
        em.merge(category);
    }

    public void removeCategory(Category category) {
        if (category == null) {
            return;
        }
        em.remove(category);
    }

    public void removeCategory(UUID categoryId) {
        if (categoryId == null) {
            return;
        }
        Category category = em.find(Category.class, categoryId);
        em.remove(category);
    }
}

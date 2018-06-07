package ru.geekbrains.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;

import java.util.List;

@Component
@Transactional
public class AdvertisementDAO extends AbstractDAO {

    public List<Advertisement> getAdvertisements() {
        return em.createQuery("SELECT ad FROM Advertisement ad", Advertisement.class)
            .getResultList();
    }

    /**
     * Получить категории для объявления
     *
     * @param advertisement - объявление
     * @return - список категорий
     */
    public List<Category> getCategories(Advertisement advertisement) {
        return em.createQuery("SELECT ad.categories FROM Advertisement ad WHERE ad.id = :id")
            .setParameter("id", advertisement.getId())
            .getResultList();
    }

    public Advertisement merge(Advertisement advertisement) {
        return em.merge(advertisement);
    }

}

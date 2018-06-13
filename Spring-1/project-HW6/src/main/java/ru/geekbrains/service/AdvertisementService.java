package ru.geekbrains.service;

import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;

import java.util.List;
import java.util.Optional;

public interface AdvertisementService {
    /**
     * Добавление объявления
     *
     * @param advertisement - объявление
     */
    void addAdvertisement(Advertisement advertisement);

    /**
     * Удаление объявления
     *
     * @param advertisement - объявление
     */
    void removeAdvertisement(Advertisement advertisement);

    /**
     * Получение объявления по id
     *
     * @param id - id объявления
     * @return - объявление
     */
    Optional<Advertisement> getAdvertisementById(String id);

    /**
     * Обновление атрибутов объявления
     *
     * @param advertisement - обновлённое объявление
     */
    void updateAdvertisement(Advertisement advertisement);

    /**
     * Получение всех объявлений
     *
     * @return - список объявлений
     */
    List<Advertisement> getAllAdvertisements();

    /**
     * Получение всех объявлений из данной категории
     *
     * @param category - категория
     * @return - объявления из данной категории
     */
    List<Advertisement> getAdvertisementsByCategory(Category category);
}

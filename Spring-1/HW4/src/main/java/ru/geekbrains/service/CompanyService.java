package ru.geekbrains.service;

import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Company;

import java.util.Optional;

public interface CompanyService {
    /**
     * Добавить компанию
     *
     * @param company - компания
     */
    void addCompany(Company company);

    /**
     * Получение компании, которой принадлежит данное объявление
     *
     * @param advertisement - объявление
     * @return - компания
     */
    Optional<Company> getCompanyByAdvertisement(Advertisement advertisement);

    /**
     * Получение компании по id
     *
     * @param id - id компании
     * @return - компания
     */
    Optional<Company> getCompanyById(String id);
}

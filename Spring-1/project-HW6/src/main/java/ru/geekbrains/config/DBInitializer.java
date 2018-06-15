package ru.geekbrains.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;
import ru.geekbrains.entity.Company;
import ru.geekbrains.repository.AdvertisementRepository;
import ru.geekbrains.repository.CategoryRepository;
import ru.geekbrains.repository.CompanyRepository;

import javax.annotation.PostConstruct;

@Component
public class DBInitializer {
    private final CategoryRepository categoryRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CompanyRepository companyRepository;

    public DBInitializer(CategoryRepository categoryRepository, AdvertisementRepository advertisementRepository,
                         CompanyRepository companyRepository) {
        this.categoryRepository = categoryRepository;
        this.advertisementRepository = advertisementRepository;
        this.companyRepository = companyRepository;
    }

    @PostConstruct
    private void fillCategoryTable() {
        clearTables();

        Category booksCat = new Category("books");
        Category electronicsCat = new Category("electronics");
        Category jobsCat = new Category("jobs");

        Company dmkPressCompany = new Company("ДМК-Пресс", "Издательство", "Москва");
        Company pleerRuCompany = new Company("www.pleer.ru", "Магазин электроники", "Москва");

        categoryRepository.save(booksCat);
        categoryRepository.save(electronicsCat);
        categoryRepository.save(jobsCat);

        companyRepository.save(dmkPressCompany);
        companyRepository.save(pleerRuCompany);

        advertisementRepository.save(new Advertisement(booksCat, "Философия Java", "Философия Java", dmkPressCompany));
        advertisementRepository.save(new Advertisement(booksCat, "Запускаем Ansible", "Запускаем Ansible", dmkPressCompany));
        advertisementRepository.save(new Advertisement(electronicsCat, "Samsung Galaxy S8+", "Samsung Galaxy S8+", pleerRuCompany));
    }

    @Transactional
    void clearTables() {
        advertisementRepository.deleteAll();
        companyRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}

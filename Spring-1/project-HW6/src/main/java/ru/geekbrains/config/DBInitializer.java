package ru.geekbrains.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Category;
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

        categoryRepository.save(new Category("books"));
        categoryRepository.save(new Category("electronics"));
        categoryRepository.save(new Category("jobs"));
    }

    @Transactional
    void clearTables() {
        advertisementRepository.deleteAll();
        companyRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}

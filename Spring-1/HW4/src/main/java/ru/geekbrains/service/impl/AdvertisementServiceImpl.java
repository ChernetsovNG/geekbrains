package ru.geekbrains.service.impl;

import org.springframework.stereotype.Service;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;
import ru.geekbrains.entity.Company;
import ru.geekbrains.repository.AdvertisementRepository;
import ru.geekbrains.repository.CategoryRepository;
import ru.geekbrains.repository.CompanyRepository;
import ru.geekbrains.service.AdvertisementService;

import java.util.List;
import java.util.Optional;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final CompanyRepository companyRepository;

    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository,
                                    CategoryRepository categoryRepository, CompanyRepository companyRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public void addAdvertisement(Advertisement advertisement) {
        advertisementRepository.save(advertisement);
    }

    @Override
    public void removeAdvertisement(Advertisement advertisement) {
        // Обновляем компании
        List<Company> companies = companyRepository.findCompaniesByAdvertisementsContains(advertisement);
        for (Company company : companies) {
            company.removeAdvertisement(advertisement);
        }
        // Обновляем категории
        for (Category category : advertisement.getCategories()) {
            category.removeAdvertisement(advertisement);
        }
        advertisementRepository.delete(advertisement);
    }

    @Override
    public Optional<Advertisement> getAdvertisementById(String id) {
        return advertisementRepository.findById(id);
    }

    @Override
    public void updateAdvertisement(Advertisement advertisement) {
        advertisementRepository.save(advertisement);
    }

    @Override
    public List<Advertisement> getAllAdvertisements() {
        return advertisementRepository.findAll();
    }

    @Override
    public List<Advertisement> getAdvertisementsByCategory(Category category) {
        return categoryRepository.getByName(category.getName()).getAdvertisements();
    }
}

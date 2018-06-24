package ru.geekbrains.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.repository.AdvertisementRepository;
import ru.geekbrains.repository.CompanyRepository;
import ru.geekbrains.service.AdvertisementService;

import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CompanyRepository companyRepository;

    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository, CompanyRepository companyRepository) {
        this.advertisementRepository = advertisementRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<Advertisement> getAll() {
        return advertisementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Advertisement> getAll(Pageable pageable) {
        return advertisementRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Advertisement get(String id) {
        return advertisementRepository.getById(id);
    }

    @Override
    @Transactional
    public void save(Advertisement advertisement) {
        companyRepository.save(advertisement.getCompany());
        advertisementRepository.save(advertisement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getByCategoryId(String id) {
        return advertisementRepository.findByCategoryId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Advertisement> getByCategoryId(String id, Pageable pageable) {
        return advertisementRepository.findByCategoryId(id, pageable);
    }

}

package ru.geekbrains.service.impl;

import org.springframework.stereotype.Service;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Company;
import ru.geekbrains.repository.CompanyRepository;
import ru.geekbrains.service.CompanyService;

import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public void addCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public Optional<Company> getCompanyByAdvertisement(Advertisement advertisement) {
        return Optional.ofNullable(companyRepository.findCompanyByAdvertisementsContaining(advertisement));
    }

    @Override
    public Optional<Company> getCompanyById(String id) {
        return companyRepository.findById(id);
    }
}

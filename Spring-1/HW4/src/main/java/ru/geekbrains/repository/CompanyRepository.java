package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Company;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Company findCompanyByAdvertisementsContaining(Advertisement advertisement);

    List<Company> findCompaniesByAdvertisementsContains(Advertisement advertisement);

    Optional<Company> findById(String id);
}

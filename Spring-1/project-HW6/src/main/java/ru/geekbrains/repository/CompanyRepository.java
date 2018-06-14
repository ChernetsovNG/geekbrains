package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
}

package ru.geekbrains.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Company;

import java.util.List;

@Component
@Transactional
public class CompanyDAO extends AbstractDAO {

    public List<Company> getCompanies() {
        return em.createQuery("SELECT c FROM Company c", Company.class)
            .getResultList();
    }

    public Company merge(Company company) {
        return em.merge(company);
    }

    public void addAdvertisement(Company company, Advertisement advertisement) {
        Company companyFromDB = em.find(Company.class, company.getId());
        if (companyFromDB != null) {
            companyFromDB.addAdvertisement(advertisement);
            merge(companyFromDB);
        }
    }

    public void addAdvertisements(Company company, List<Advertisement> advertisements) {
        Company companyFromDB = em.find(Company.class, company.getId());
        if (companyFromDB != null) {
            companyFromDB.addAdvertisements(advertisements);
            merge(companyFromDB);
        }
    }

    public List<Advertisement> getAdvertisements(Company company) {
        return em.createQuery("SELECT c.advertisements FROM Company c WHERE c.id = :id")
            .setParameter("id", company.getId())
            .getResultList();
    }
}

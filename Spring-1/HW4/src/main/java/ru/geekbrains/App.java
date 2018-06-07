package ru.geekbrains;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.geekbrains.dao.AdvertisementDAO;
import ru.geekbrains.dao.CategoryDAO;
import ru.geekbrains.dao.CompanyDAO;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;
import ru.geekbrains.entity.Company;

import java.util.Arrays;
import java.util.Collections;

public class App {

    public static void main(String[] args) {
        final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        final AdvertisementDAO advertisementDAO = context.getBean(AdvertisementDAO.class);
        final CategoryDAO categoryDAO = context.getBean(CategoryDAO.class);
        final CompanyDAO companyDAO = context.getBean(CompanyDAO.class);

        // Создаём категории
        Category books = new Category("books");
        Category electronics = new Category("electronics");
        Category jobs = new Category("jobs");

        categoryDAO.merge(books);
        categoryDAO.merge(electronics);
        categoryDAO.merge(jobs);

        System.out.println(categoryDAO.getCategories());

        // Создаём компании
        Company pleerRu = new Company("Pleer.ru", "Магазин электроники", "Автозаводская");
        Company dmkPress = new Company("ДМК-пресс", "Издательство", "");

        companyDAO.merge(pleerRu);
        companyDAO.merge(dmkPress);

        // Создаём объявления
        Advertisement bookAd1 = new Advertisement(Collections.singletonList(books), "Путь Ruby", "", "");
        Advertisement bookAd2 = new Advertisement(Collections.singletonList(books), "Python. К вершинам мастерства", "", "");

        advertisementDAO.merge(bookAd1);
        advertisementDAO.merge(bookAd2);
        categoryDAO.merge(books);

        companyDAO.addAdvertisements(dmkPress, Arrays.asList(bookAd1, bookAd2));

        System.out.println(companyDAO.getAdvertisements(dmkPress));
    }

}

package ru.geekbrains;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;
import ru.geekbrains.entity.Company;
import ru.geekbrains.service.AdvertisementService;
import ru.geekbrains.service.CategoryService;
import ru.geekbrains.service.CompanyService;

public class App {

    public static void main(String[] args) {
        final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Перед использованием сервисов заполняем базу данных тестовыми данными
        AdvertisementService advertisementService = context.getBean(AdvertisementService.class);
        CategoryService categoryService = context.getBean(CategoryService.class);
        CompanyService companyService = context.getBean(CompanyService.class);

        // Создаём категории
        Category books = new Category("books");
        Category electronics = new Category("electronics");
        Category jobs = new Category("jobs");

        categoryService.addCategory(books);
        categoryService.addCategory(electronics);
        categoryService.addCategory(jobs);

        // Создаём компании
        Company pleerRu = new Company("Pleer.ru", "Магазин электроники", "Автозаводская");
        Company dmkPress = new Company("ДМК-пресс", "Издательство", "");

        // Создаём объявления
        Advertisement bookAd1 = new Advertisement("Путь Ruby", "", "");
        Advertisement bookAd2 = new Advertisement("Python. К вершинам мастерства", "", "");
        Advertisement electronicsAd1 = new Advertisement("Samsung Galaxy S8+", "", "");

        bookAd1.addCategory(books);
        bookAd2.addCategory(books);
        electronicsAd1.addCategory(electronics);

        dmkPress.addAdvertisement(bookAd1);
        dmkPress.addAdvertisement(bookAd2);
        pleerRu.addAdvertisement(electronicsAd1);

        advertisementService.addAdvertisement(bookAd1);
        advertisementService.addAdvertisement(bookAd2);
        advertisementService.addAdvertisement(electronicsAd1);

        companyService.addCompany(pleerRu);
        companyService.addCompany(dmkPress);

        // пробуем поработать с сервисами
        advertisementService.removeAdvertisement(bookAd1);
    }

}

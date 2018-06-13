package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Category getByName(String name);

    List<Category> findAll();
}

package ru.geekbrains.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Advertisement;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {
    List<Advertisement> findAll();

    Advertisement getById(String id);

    @Query("SELECT a FROM Advertisement a WHERE a.category.id=:id")
    List<Advertisement> findByCategoryId(@Param("id") String id);

    @Query("SELECT a FROM Advertisement a WHERE a.category.id=:id")
    Page<Advertisement> findByCategoryId(@Param("id") String id, Pageable pageable);

    @Query("SELECT a FROM Advertisement a WHERE a.category.name=:name")
    Page<Advertisement> findByCategoryName(@Param("name") String name, Pageable pageable);
}

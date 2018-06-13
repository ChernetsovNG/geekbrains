package ru.geekbrains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.entity.Advertisement;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {
    Optional<Advertisement> findById(String id);

    List<Advertisement> findAll();
}

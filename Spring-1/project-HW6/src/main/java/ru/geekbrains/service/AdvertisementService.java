package ru.geekbrains.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.geekbrains.entity.Advertisement;

import java.util.List;

public interface AdvertisementService {

    List<Advertisement> getAll();

    Page<Advertisement> getAll(Pageable pageable);

    Advertisement get(String id);

    void save(Advertisement advertisement);

    List<Advertisement> getByCategoryId(String id);

    Page<Advertisement> getByCategoryId(String id, Pageable pageable);
}

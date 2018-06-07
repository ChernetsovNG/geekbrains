package ru.geekbrains.service;

import org.springframework.stereotype.Service;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.repository.AdvertisementRepository;

@Service
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    public AdvertisementService(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    void addAdvertisement(Advertisement advertisement) {
        advertisementRepository.save(advertisement);
    }

    void removeAdvertisement(Advertisement advertisement) {
        advertisementRepository.delete(advertisement);
    }


}

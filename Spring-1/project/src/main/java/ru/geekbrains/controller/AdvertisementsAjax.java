package ru.geekbrains.controller;

import ru.geekbrains.entity.Advertisement;

import java.util.List;

public class AdvertisementsAjax {

    private List<Advertisement> advertisements;

    public List<Advertisement> getAdvertisements() {
        return advertisements;
    }

    void setAdvertisements(List<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }

}

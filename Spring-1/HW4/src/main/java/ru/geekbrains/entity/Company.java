package ru.geekbrains.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "company")
public class Company extends AbstractEntity {
    private String name;

    private String description;

    private String address;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
            orphanRemoval = true)
    private final List<Advertisement> advertisements = new ArrayList<>();

    public Company() {
    }

    public Company(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public void addAdvertisement(Advertisement advertisement) {
        getAdvertisements().add(advertisement);
    }

    public void addAdvertisements(Collection<Advertisement> advertisements) {
        getAdvertisements().addAll(advertisements);
    }

    public void removeAdvertisement(Advertisement advertisement) {
        getAdvertisements().remove(advertisement);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Advertisement> getAdvertisements() {
        return advertisements;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

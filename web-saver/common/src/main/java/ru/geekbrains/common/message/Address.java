package ru.geekbrains.common.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class Address implements Serializable {
    private final String address;

    public Address(String address) {
        this.address = address;
    }
}

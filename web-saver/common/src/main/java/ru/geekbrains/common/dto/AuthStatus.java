package ru.geekbrains.common.dto;

import java.io.Serializable;

public enum AuthStatus implements Serializable {
    INCORRECT_USERNAME,
    INCORRECT_PASSWORD,
    AUTH_OK;
}

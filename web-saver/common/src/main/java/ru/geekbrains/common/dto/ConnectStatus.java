package ru.geekbrains.common.dto;

import java.io.Serializable;

public enum ConnectStatus implements Serializable {
    HANDSHAKE_OK,
    INCORRECT_USERNAME,
    INCORRECT_PASSWORD,
    REGISTER_OK,
    REGISTER_ERROR,
    ALREADY_REGISTER,
    NOT_REGISTER,
    AUTH_OK,
    ALREADY_AUTH;
}

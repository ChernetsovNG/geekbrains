package ru.geekbrains.common.dto;

import java.io.Serializable;

public enum ConnectStatus implements Serializable {
    HANDSHAKE_OK,
    INCORRECT_USERNAME,
    INCORRECT_PASSWORD,
    AUTH_OK;
}

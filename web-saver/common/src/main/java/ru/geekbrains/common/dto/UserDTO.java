package ru.geekbrains.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class UserDTO implements Serializable {
    @Setter
    private int id = 0;
    private final String name;
    private final String password;

    public UserDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public UserDTO(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}

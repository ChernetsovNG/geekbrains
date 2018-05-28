package ru.geekbrains.beans;

import java.util.UUID;

/**
 * Патрон
 */
public class Bullet {
    private final UUID id;

    public Bullet() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Bullet{" +
            "id=" + id +
            '}';
    }
}

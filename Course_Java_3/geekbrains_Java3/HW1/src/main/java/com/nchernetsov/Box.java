package com.nchernetsov;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Box<T extends Fruit> {
    private final List<T> fruits = new ArrayList<T>();

    // Добавить фрукт в коробку
    public void addFruit(T fruit) {
        fruits.add(fruit);
    }

    // Вычисляем суммарный вес фруктов в коробке
    public float getWeight() {
        float weight = 0.0f;
        for (T fruit : fruits) {
            weight += fruit.getWeigth();
        }
        return weight;
    }

    // Сравнить вес текущей коробки с другой
    public boolean compare(Box<?> another) {
        float delta = 1e-3f;

        // Проверяем, что веса отличаются не больше, чем на заданную величину
        return abs(this.getWeight() - another.getWeight()) < delta;
    }

    // Пересыпаем все фрукты из данной коробки в другую
    public void shiftFruitsIntoAnotherBox(Box<T> another) {
        for (T fruit : fruits) {
            another.addFruit(fruit);
        }
        fruits.clear();
    }
}

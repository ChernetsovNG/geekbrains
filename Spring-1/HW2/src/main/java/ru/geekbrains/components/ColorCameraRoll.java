package ru.geekbrains.components;

import org.springframework.stereotype.Component;

@Component("colorCameraRoll")
public class ColorCameraRoll implements CameraRoll {
    @Override
    public void processing() {
        System.out.println("Цветной кадр");
    }
}

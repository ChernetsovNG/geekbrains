package ru.geekbrains.components;

import org.springframework.stereotype.Component;
import ru.geekbrains.annotation.UnproductableCameraRoll;

@Component("blackAndWhiteCameraRoll")
// @UnproductableCameraRoll(usingCameraRollClass = ColorCameraRoll.class)
public class BlackAndWhiteCameraRoll implements CameraRoll {
    public void processing() {
        System.out.println("Чёрно-белый кадр");
    }
}

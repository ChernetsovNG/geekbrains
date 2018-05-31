package ru.geekbrains.components;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Client {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Camera camera = context.getBean("camera", Camera.class);
        // потребуем цветную фотоплёнку
        camera.setCameraRoll(context.getBean("colorCameraRoll", CameraRoll.class));
        // а класс настройщик CameraBeanFactoryPostProcessor подменит её на чёрно-белую
        camera.doPhotograph();
    }
}

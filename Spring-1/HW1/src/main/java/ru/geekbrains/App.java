package ru.geekbrains;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.geekbrains.beans.Bullet;
import ru.geekbrains.beans.Charger;
import ru.geekbrains.beans.Rifle;

public class App {

    // Пример конфигуриации Spring при помощи xml
    public static void main(String[] args) {
        App app = new App();

        app.xmlConfigTest();
        app.javaConfigTest();
    }

    private void xmlConfigTest() {
        System.out.println("XML config test");
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");

        Rifle rifle = context.getBean("rifle-xml", Rifle.class);

        Charger charger = rifle.getCharger();
        for (int i = 0; i < 10; i++) {
            charger.addBullet(context.getBean("bullet-xml", Bullet.class));
        }

        rifle.fire();
        rifle.fire();
        rifle.fire();
    }

    private void javaConfigTest() {
        System.out.println("Java config test");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Rifle rifle = context.getBean("rifle-ann", Rifle.class);

        Charger charger = rifle.getCharger();
        for (int i = 0; i < 10; i++) {
            charger.addBullet(context.getBean("bullet-ann", Bullet.class));
        }

        rifle.fire();
        rifle.fire();
        rifle.fire();
    }

}

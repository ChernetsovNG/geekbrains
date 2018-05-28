package ru.geekbrains;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.geekbrains.beans.Bullet;
import ru.geekbrains.beans.Charger;
import ru.geekbrains.beans.Rifle;

@Configuration
public class AppConfig {

    @Bean(name = "bullet-ann")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Bullet bullet() {
        return new Bullet();
    }

    @Bean(name = "charger-ann")
    public Charger charger() {
        Charger charger = new Charger();
        charger.setMaxSize(12);
        return charger;
    }

    @Bean(name = "rifle-ann")
    public Rifle rifle(@Qualifier("charger-ann") Charger charger) {
        Rifle rifle = new Rifle();
        rifle.setCharger(charger);
        return rifle;
    }

}

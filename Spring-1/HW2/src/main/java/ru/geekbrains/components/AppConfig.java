package ru.geekbrains.components;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"ru.geekbrains.components", "ru.geekbrains.components.lifecycle"})
public class AppConfig {
}

package ru.geekbrains.components.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.geekbrains.components.Camera;

@Component
public class PhotocameraTestBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Camera) {
            System.out.println("Делаю пробное фото!");
            ((Camera) bean).doPhotograph();
            System.out.println("Работает");
        }
        return bean;
    }
}

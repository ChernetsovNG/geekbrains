package com.nchernetsov;

import com.nchernetsov.annotation.AfterSuite;
import com.nchernetsov.annotation.BeforeSuite;
import com.nchernetsov.annotation.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.nchernetsov.ReflectionHelper.callMethod;
import static com.nchernetsov.ReflectionHelper.getMethodsAnnotatedWith;
import static com.nchernetsov.ReflectionHelper.instantiate;

// Класс - "тестировщик"
public class Tester {

    public static void start(String className) {
        try {
            start(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void start(Class<?> clazz) {
        Object object = instantiate(clazz);

        // Проверяем, что методов @BeforeSuite и @AfterSuite не больше одного
        checkThatMethodsCountNoMoreThanOne(clazz, BeforeSuite.class);
        checkThatMethodsCountNoMoreThanOne(clazz, AfterSuite.class);

        // Метод @BeforeSuite
        executeOnlyOneTestMethod(clazz, object, BeforeSuite.class);
        // Методы @Test
        executeTestMethods(clazz, object);
        // Метод @AfterSuite
        executeOnlyOneTestMethod(clazz, object, AfterSuite.class);
    }

    private static void checkThatMethodsCountNoMoreThanOne(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> methods = getMethodsAnnotatedWith(clazz, annotation);
        if (methods.size() > 1) {
            throw new RuntimeException("There can be only one method, annotated with " + annotation.getSimpleName());
        }
    }

    // Выполнить метод, который должен быть только один (@BeforeSuite или @AfterSuite)
    private static void executeOnlyOneTestMethod(Class<?> clazz, Object object, Class<? extends Annotation> annotation) {
        List<Method> methods = getMethodsAnnotatedWith(clazz, annotation);
        if (methods.size() == 1) {
            Method method = methods.get(0);
            callMethod(object, method.getName());
        }
    }

    // Выполнить методы, помеченные аннотацией @Test
    private static void executeTestMethods(Class<?> clazz, Object object) {
        List<Method> methods = getMethodsAnnotatedWith(clazz, Test.class);

        // Фильтруем методы, оставляя методы с приоритетом от 1 до 10, и сортируем отфильтрованные методы по возрастанию приоритета
        List<Method> methodsToInvoke = methods.stream()
            .filter(m -> m.getAnnotation(Test.class).priority() >= 1 && m.getAnnotation(Test.class).priority() <= 10)
            .sorted(Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority()))
            .collect(Collectors.toList());

        for (Method method : methodsToInvoke) {
            callMethod(object, method.getName());
            System.out.println("Test method: " + method.getName() + " ok\n");
        }
    }
}

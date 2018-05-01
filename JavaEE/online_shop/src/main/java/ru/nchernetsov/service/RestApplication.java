package ru.nchernetsov.service;

import ru.nchernetsov.service.rest.ProductRS;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {

    public Set<Class<?>> getClasses() {
        final Set<Class<?>> result = new HashSet<>();
        result.add(ProductRS.class);
        return result;
    }
}

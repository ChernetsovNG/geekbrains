package ru.nchernetsov.orm;

import ru.nchernetsov.entity.User;

interface Executor {
    void save(User user);
    User load(long id, Class<?> clazz);
}

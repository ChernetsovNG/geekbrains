package ru.nchernetsov.database_mapper.orm;

import ru.nchernetsov.database_mapper.entity.User;

interface Executor {
    void save(User user);
    User load(long id, Class<?> clazz);
}

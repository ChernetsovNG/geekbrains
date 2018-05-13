package ru.nchernetsov.database_mapper.orm;

import java.util.List;
import ru.nchernetsov.database_mapper.entity.User;

interface Executor {

  void save(User user);

  User load(long id, Class<?> clazz);

  List<User> loadAll(Class<?> clazz);
}

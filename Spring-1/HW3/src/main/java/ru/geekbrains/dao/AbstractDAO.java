package ru.geekbrains.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

abstract class AbstractDAO {
    @PersistenceContext(name = "persistenceUnit")
    EntityManager em;
}

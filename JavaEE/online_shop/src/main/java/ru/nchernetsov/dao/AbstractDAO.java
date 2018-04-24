package ru.nchernetsov.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

abstract class AbstractDAO {

    @PersistenceContext(unitName = "java-ee-persistence-unit")
    EntityManager em;

}

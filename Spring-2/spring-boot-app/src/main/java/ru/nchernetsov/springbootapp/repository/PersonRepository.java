package ru.nchernetsov.springbootapp.repository;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import ru.nchernetsov.springbootapp.model.Person;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PersonRepository {

    private Map<String, Person> persons = new LinkedHashMap<>();

    @PostConstruct
    private void init() {
        merge(new Person("Иванов", "Иван", "Иванович", ""));
        merge(new Person("Петров", "Петр", "Олегович", ""));
    }

    public Collection<Person> getListPerson() {
        return persons.values();
    }

    public void merge(Person person) {
        if (person == null) return;
        if (person.getId() == null || person.getId().isEmpty()) return;
        persons.put(person.getId(), person);
    }

    public Person findOne(String personId) {
        if (personId == null || personId.isEmpty()) return null;
        return persons.get(personId);
    }

    public void removePersonById(String personId) {
        if (personId == null || personId.isEmpty()) return;
        persons.remove(personId);
    }

}

package ru.nchernetsov.springbootapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nchernetsov.springbootapp.model.Person;
import ru.nchernetsov.springbootapp.repository.PersonRepository;

import java.util.Map;

@Controller
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping(value = "/person-list")
    public String personList(Map<String, Object> model) {
        model.put("persons", personRepository.getListPerson());
        return "person-list";
    }

    @GetMapping(value = "/person-create")
    public String personCreate() {
        personRepository.merge(new Person());
        return "redirect:/person-list";
    }

    @GetMapping(value = "/person-remove")
    public String personRemove(@RequestParam("id") String personId) {
        personRepository.removePersonById(personId);
        return "redirect:/person-list";
    }

    @GetMapping(value = "/person-edit")
    public String personEdit(@RequestParam("id") String personId, Map<String, Object> model) {
        final Person person = personRepository.findOne(personId);
        model.put("person", person);
        return "person-edit";
    }

    @PostMapping(value = "/person-save")
    public String personSave(@ModelAttribute("person") Person person) {
        personRepository.merge(person);
        return "redirect:/person-list";
    }

}

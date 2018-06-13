package ru.geekbrains.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoryController {

    @GetMapping(value = "/categories/{id}")
    public void view(@PathVariable("id") Integer id) {

    }

    @GetMapping(value = "/categories/{id}/articles_ajax")
    public void viewAjax(@PathVariable("id") Integer id) {

    }
}

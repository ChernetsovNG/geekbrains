package ru.geekbrains.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/categories")
public class CategoryController {

    @GetMapping(value = "/{id}")
    public void view(@PathVariable("id") Integer id) {

    }

    @GetMapping(value = "/{id}/articles_ajax")
    public void viewAjax(@PathVariable("id") Integer id) {

    }
}

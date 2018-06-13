package ru.geekbrains.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArticleController {

    @GetMapping(value = "/articles")
    public void list() {

    }

    @GetMapping(value = "/articles/{id}")
    public void view(@PathVariable("id") Integer id) {

    }

    @GetMapping(value = "/articles/add")
    public void addForm() {

    }

    @PostMapping(value = "/articles")
    public void add() {

    }

    @GetMapping(value = "/articles/articles_ajax")
    public void listAjax() {

    }
}

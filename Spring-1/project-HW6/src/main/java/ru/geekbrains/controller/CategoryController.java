package ru.geekbrains.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.entity.Article;
import ru.geekbrains.entity.Category;
import ru.geekbrains.service.ArticleService;
import ru.geekbrains.service.CategoryService;

import java.util.List;

import static ru.geekbrains.utils.Utils.iteratorToList;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    public CategoryController(ArticleService articleService, CategoryService categoryService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/{id}")
    public String view(@PathVariable("id") String id, Model model) {
        Category category = categoryService.get(id);
        model.addAttribute("category", category);
        List<Category> categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        return "category/view";
    }

    @GetMapping(value = "/{id}/articles_ajax", produces = "application/json")
    @ResponseBody
    public ArticlesAjax viewAjax(@PathVariable("id") String id,
                                 @RequestParam("pageCounter") Integer pageCounter,
                                 @RequestParam("number") Integer number,
                                 @RequestParam("order") String order,
                                 @RequestParam("orderBy") String orderBy) {
        Sort sort;
        if (order.equalsIgnoreCase("DESC")) {
            sort = new Sort(Sort.Direction.DESC, orderBy);
        } else {
            sort = new Sort(Sort.Direction.ASC, orderBy);
        }
        PageRequest pageable = PageRequest.of(pageCounter, number, sort);
        Page<Article> articlePage = articleService.getByCategoryId(id, pageable);
        ArticlesAjax responsive = new ArticlesAjax();
        responsive.setArticles(iteratorToList(articlePage.iterator()));

        return responsive;
    }

}

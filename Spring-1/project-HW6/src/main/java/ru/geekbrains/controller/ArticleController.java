package ru.geekbrains.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.entity.Article;
import ru.geekbrains.entity.Author;
import ru.geekbrains.entity.Category;
import ru.geekbrains.service.ArticleService;
import ru.geekbrains.service.CategoryService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final CategoryService categoryService;

    public ArticleController(ArticleService articleService, CategoryService categoryService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list() {
        return "redirect:/";
    }

    /**
     * @param id    - идентификатор статьи
     * @param model - данные
     * @return путь к странице отображения статьи
     */
    @GetMapping(value = "/{id}")
    public String view(@PathVariable("id") String id, Model model) {
        Article article = articleService.get(id);
        model.addAttribute("article", article);
        return "article/view";
    }

    @GetMapping(value = "/add")
    public String addForm(Model model) {
        // создание пустого объекта
        Article article = new Article();
        article.setAuthor(new Author());
        //получение списка всех категорий для возможности выбора категории, к которой будет принадлежать создаваемая статья
        List<Category> categories = categoryService.getAll();
        // связывание объекта статьи с формой и добавление списка категорий на страницу
        model
                .addAttribute("article", article)
                .addAttribute("categories", categories);
        return "article/add";
    }

    @PostMapping
    public String add(@ModelAttribute("article") Article article,
                      BindingResult bindingResult,
                      @RequestParam("categoryId") String categoryId) {
        Category category = categoryService.get(categoryId);
        if (bindingResult.hasErrors() || category == null) {
            return "redirect:/articles/add";
        }
        article.setCategory(category);
        articleService.save(article);
        return "redirect:/";
    }

    /**
     * Метод обрабатывающий асинхронный запрос
     *
     * @param pageCounter-текущая страница(блок из number статей)
     * @param number              - количество статей в одном блоке
     * @param order               - порядок сортировки(ASC-прямая, DESC-обратная)
     * @param orderBy             - поле по которому происходит сортировка
     * @return объект класса ArticlesAjax, который содержит список статей,
     * данный объект преобразовывается в JSON-формат
     */
    @GetMapping(value = "/articles_ajax", produces = "application/json")
    @ResponseBody
    public ArticlesAjax listAjax(@RequestParam("pageCounter") Integer pageCounter,
                                 @RequestParam("number") Integer number,
                                 @RequestParam("order") String order,
                                 @RequestParam("orderBy") String orderBy) {
        // объект, который будет содержать информацию о сортировке
        Sort sort;

        if (order.equalsIgnoreCase("DESC")) {
            // конструктор Sort принимает в качестве параметров тип сортировки и поле,
            // по которому будет происходить соритровка
            sort = new Sort(Sort.Direction.DESC, orderBy);
        } else {
            sort = new Sort(Sort.Direction.ASC, orderBy);
        }
        // конструктор принимает полную информацию о текущем блоке, количестве статей и сортировке
        PageRequest pageable = PageRequest.of(pageCounter, number, sort);

        Page<Article> articlePage = articleService.getAll(pageable);

        ArticlesAjax responsive = new ArticlesAjax();
        // из объекта Page возвращаем итератор и с помощью библиотеки google guava создаем списочный массив
        Iterator<Article> articleIterator = articlePage.iterator();
        List<Article> articles = new ArrayList<>();
        while (articleIterator.hasNext()) {
            articles.add(articleIterator.next());
        }
        responsive.setArticles(articles);
        return responsive;
    }

}

package ru.geekbrains.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;
import ru.geekbrains.service.AdvertisementService;
import ru.geekbrains.service.CategoryService;

import java.util.List;

import static ru.geekbrains.utils.Utils.iteratorToList;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final AdvertisementService advertisementService;
    private final CategoryService categoryService;

    public CategoryController(AdvertisementService advertisementService, CategoryService categoryService) {
        this.advertisementService = advertisementService;
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

    @GetMapping(value = "/{id}/advertisements_ajax", produces = "application/json")
    @ResponseBody
    public AdvertisementsAjax viewAjax(@PathVariable("id") String id,
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
        Page<Advertisement> advertisementsPage = advertisementService.getByCategoryId(id, pageable);
        AdvertisementsAjax responsive = new AdvertisementsAjax();
        responsive.setAdvertisements(iteratorToList(advertisementsPage.iterator()));

        return responsive;
    }

}

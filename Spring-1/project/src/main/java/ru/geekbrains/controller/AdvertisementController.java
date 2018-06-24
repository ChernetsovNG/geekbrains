package ru.geekbrains.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.entity.Advertisement;
import ru.geekbrains.entity.Category;
import ru.geekbrains.entity.Company;
import ru.geekbrains.service.AdvertisementService;
import ru.geekbrains.service.CategoryService;

import java.util.List;

import static ru.geekbrains.utils.Utils.iteratorToList;

@Controller
@RequestMapping("/advertisements")
public class AdvertisementController {
    private final AdvertisementService advertisementService;
    private final CategoryService categoryService;

    public AdvertisementController(AdvertisementService advertisementService, CategoryService categoryService) {
        this.advertisementService = advertisementService;
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
        Advertisement advertisement = advertisementService.get(id);
        model.addAttribute("advertisement", advertisement);
        return "advertisement/view";
    }

    @GetMapping(value = "/add")
    public String addForm(Model model) {
        // создание пустого объекта
        Advertisement advertisement = new Advertisement();
        advertisement.setCompany(new Company());
        // получение списка всех категорий для возможности выбора категории, к которой будет принадлежать объявление
        List<Category> categories = categoryService.getAll();
        // связывание объекта статьи с формой и добавление списка категорий на страницу
        model.addAttribute("advertisement", advertisement)
                .addAttribute("categories", categories);
        return "advertisement/add";
    }

    @PostMapping
    public String add(@ModelAttribute("advertisement") Advertisement advertisement,
                      BindingResult bindingResult,
                      @RequestParam("categoryId") String categoryId) {
        Category category = categoryService.get(categoryId);
        if (bindingResult.hasErrors() || category == null) {
            return "redirect:/advertisements/add";
        }
        advertisement.setCategory(category);
        advertisementService.save(advertisement);
        return "redirect:/";
    }

    /**
     * Метод обрабатывающий асинхронный запрос
     *
     * @param pageCounter-текущая страница(блок из number статей)
     * @param number              - количество статей в одном блоке
     * @param order               - порядок сортировки(ASC-прямая, DESC-обратная)
     * @param orderBy             - поле по которому происходит сортировка
     * @return объект класса AdvertisementsAjax, который содержит список статей,
     * данный объект преобразовывается в JSON-формат
     */
    @GetMapping(value = "/advertisements_ajax", produces = "application/json")
    @ResponseBody
    public AdvertisementsAjax listAjax(@RequestParam("pageCounter") Integer pageCounter,
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

        Page<Advertisement> advertisementPage = advertisementService.getAll(pageable);

        AdvertisementsAjax responsive = new AdvertisementsAjax();
        // из объекта Page возвращаем итератор и преобразуем его в список
        responsive.setAdvertisements(iteratorToList(advertisementPage.iterator()));
        return responsive;
    }


}

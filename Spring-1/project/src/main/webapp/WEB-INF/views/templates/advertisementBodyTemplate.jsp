<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script>
    // данные, которые передаются на сервер:
    // количество страниц
    var number = 4;
    // порядок сортировки
    var order = "DESC";
    // поле для сортировки
    var orderBy = "publishedDate";
    // счетчик страниц(блоков)
    var pageCounter = 0;

    //Шаблон для размещения описания объявления в списке
    var advertisementBody = "<div class='post_section'>" + "<h2><a class='advertisement__title' href=''></a></h2>" +
        "<strong>Дата: </span></strong><span class='advertisement__date'></span>" +
        "<strong>Компания: </strong><span class='advertisement__company'></span>"
        + "<p><div class='advertisement__content'></div>" + "<div class='cleaner'></div>"
        + "<p><div class='category'>Категория: <span class='advertisement__category'></span></div>" +
        "<div class='button float_r'><a href=' ' " +
        "class='more'>Читать далее</a></div>" + "<div class='cleaner'></div>"
        + "</div><div class='cleaner_h40'></div>";
</script>

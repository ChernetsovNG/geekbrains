//Шаблон для размещения описания статьи в списке
var advertisementBody = "<div class='post_section'>" + "<h2><a class='advertisement__title' href=''></a></h2>" +
    "<strong>Дата: </span></strong><span class='advertisement__date'></span>" +
    "<strong>Компания: </strong><span class='advertisement__company'></span>"
    + "<p><div class='advertisement__content'></div>" + "<div class='cleaner'></div>"
    + "<p><div class='category'>Категория: <span class='advertisement__category'></span></div>" +
    "<div class='button float_r'><a href=' ' " +
    "class='more'>Читать далее</a></div>" + "<div class='cleaner'></div>"
    + "</div><div class='cleaner_h40'></div>";

// данные, которые передаются на сервер:
// количество страниц
var number = 4;
// порядок сортировки
var order = "DESC";
// поле для сортировки
var orderBy = "publishedDate";
// счетчик страниц(блоков)
var pageCounter = 0;

// функция для размещения полученных данных на странице
function renderingAdvertisements(advertisements) {
    advertisements.forEach(function (advertisement) {
        var test = $(advertisementBody).find(".advertisement__title").attr("href", contextPath + "/advertisements/" + advertisement["id"]).html(advertisement["title"])
            .end().find(".advertisement__date").html(advertisement["publishedDate"])
            .end().find(".advertisement__company").html(advertisement["company"]["name"])
            .end().find(".advertisement__company").html(advertisement["company"]["description"])
            .end().find(".advertisement__company").html(advertisement["company"]["address"])
            .end().find(".advertisement__content").html(advertisement["content"].substring(0, 110) + "...")
            .end().find(".advertisement__category").html(advertisement["category"].name)
            .end().find(".more").attr("href", contextPath + "/advertisements/" + advertisement["id"])
            .end().appendTo("#templatemo_content");
    });
}

// функция для осуществления асинхронного GET запроса
function loadAdvertisements() {
    // формирование строки с данными, которые необходимо передать на сервер в метод listAjax
    var data = "pageCounter=" + pageCounter + "&" + "order=" + order + "&" + "orderBy=" + orderBy + "&" + "number=" + number;
    $.ajax({
        url: url,
        type: 'GET',
        data: data,
        cache: false,
        success: function (advertisementsResponsive) {
            if (advertisementsResponsive === 0) {
            } else {
                // если ответ содержит данные, то они размещаются на странице,
                // а счетчик страниц (блоков) увеличивается на единицу
                renderingAdvertisements(advertisementsResponsive["advertisements"]);
                pageCounter++;
            }
        }
    });
}

$(document).ready(function () {
    // первая страница (блок) статей подгружается при загрузке страницы
    loadAdvertisements();
    $(".btn_load").click(function () {
        // остальные страницы подгружаются при нажатии на кнопку "Загрузить еще"
        loadAdvertisements();
    })
});

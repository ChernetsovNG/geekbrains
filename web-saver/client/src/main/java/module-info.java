module client {
    requires javafx.graphics;
    requires slf4j.api;

    requires common;
    requires server;

    exports ru.geekbrains.client to javafx.graphics;
}
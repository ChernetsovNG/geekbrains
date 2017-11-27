module client {
    requires javafx.graphics;
    requires slf4j.api;

    requires net_lib;
    requires server;

    exports ru.geekbrains.client to javafx.graphics;
}
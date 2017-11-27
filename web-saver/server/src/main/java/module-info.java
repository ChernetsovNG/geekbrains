module server {
    requires slf4j.api;

    requires net_lib;

    exports ru.geekbrains.server to client;
}
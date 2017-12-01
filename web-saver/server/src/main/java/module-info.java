module server {
    requires slf4j.api;

    requires common;

    exports ru.geekbrains.server to client;
}
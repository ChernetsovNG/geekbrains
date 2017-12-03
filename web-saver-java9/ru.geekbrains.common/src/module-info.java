module ru.geekbrains.common {
    requires org.slf4j;
    requires lombok;

    exports ru.geekbrains.common.message;
    exports ru.geekbrains.common.channel;
    exports ru.geekbrains.common to ru.geekbrains.client, ru.geekbrains.server;
}
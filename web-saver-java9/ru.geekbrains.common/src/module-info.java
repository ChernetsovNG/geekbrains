module ru.geekbrains.common {
    requires org.slf4j;

    exports ru.geekbrains.common.channel;
    exports ru.geekbrains.common to ru.geekbrains.client, ru.geekbrains.server;
}
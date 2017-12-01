module ru.geekbrains.client {
    requires javafx.graphics;
    requires org.slf4j;
    requires ru.geekbrains.common;

    exports ru.geekbrains.client to javafx.graphics;
}
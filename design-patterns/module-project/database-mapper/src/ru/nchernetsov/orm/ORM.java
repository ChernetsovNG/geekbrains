package ru.nchernetsov.orm;

import org.reflections.Reflections;
import ru.nchernetsov.entity.User;
import ru.nchernetsov.utils.ConnectionHelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.nchernetsov.utils.ReflectionHelper.instantiate;
import static ru.nchernetsov.utils.ReflectionHelper.setFieldValue;

public class ORM implements Executor {
    private final Connection connection;
    // Карта вида (Класс - Имя таблицы в БД)
    private final Map<Class<?>, String> tableNames = new HashMap<>();
    // Карта вида (Класс - Карта (поле класса - столбец в таблице))
    private final Map<Class<?>, DataSetDescriptor> matchClassFieldsAndTablesColumnMap = new HashMap<>();

    public ORM() {
        connection = ConnectionHelper.getConnection();
        prepareObjectRelationalMapping();
    }

    private void prepareObjectRelationalMapping() {
        Reflections reflections = new Reflections("ru.otus");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Entity.class);

        // Обходим классы с аннотацией @Entity
        for (Class<?> annotatedClass : annotatedClasses) {
            // Имя таблицы, соответствующей классу
            Table annotationTable = annotatedClass.getAnnotation(Table.class);
            tableNames.put(annotatedClass, annotationTable.name());

            // Поля класса и соответствующие столбцы таблицы
            DataSetDescriptor classFieldColumnNameMap = new DataSetDescriptor();

            for (Field field : annotatedClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    String fieldName = field.getName();
                    String columnName = field.getAnnotation(Column.class).name();

                    classFieldColumnNameMap.put(fieldName, columnName);
                }
            }

            matchClassFieldsAndTablesColumnMap.put(annotatedClass, classFieldColumnNameMap);
        }
    }

    @Override
    public void save(User user) {
        long id = user.getId();
        String name = user.getName();
        int age = user.getAge();

        // Имя таблицы в БД, соответствующей сущности User
        String tableName = tableNames.get(User.class);
        // Находим имена столбцов в таблице БД, соответствующие полям класса User
        DataSetDescriptor userDescriptor = matchClassFieldsAndTablesColumnMap.get(User.class);

        String idColumnName = userDescriptor.get("id");
        String nameColumnName = userDescriptor.get("name");
        String ageColumnName = userDescriptor.get("age");

        String query = "INSERT INTO " + tableName + " (" +
            idColumnName + ", " +
            nameColumnName + ", " +
            ageColumnName + ") " +
            "VALUES (" +
            id + ", " +
            "'" + name + "'" +
            ", " + age +
            ");";

        try {
            execQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User load(long id, Class<?> clazz) {
        String tableName = tableNames.get(clazz);

        DataSetDescriptor classFieldColumnNameMap = matchClassFieldsAndTablesColumnMap.get(clazz);

        Object[] columns = classFieldColumnNameMap.values().toArray();  // столбцы таблицы

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        for (int i = 0; i < columns.length-1; i++) {
            sb.append((String) columns[i]).append(", ");
        }
        sb.append((String) columns[columns.length-1]).append(" ");
        sb.append("FROM ").append(tableName).append(" ")
            .append("WHERE id = ").append(id).append(";");

        String query = sb.toString();

        try {
            Map<String, Object> queryResultMap = execQuery(query, resultSet -> {
                Map<String, Object> map = new HashMap<>();
                resultSet.next();
                for (Object column : columns) {
                    String columnStr = (String) column;
                    map.put(columnStr, resultSet.getObject(columnStr));
                }
                return map;
            });

            // Создаём новый объект нужного класса и записываем в соответствующие
            // поля результаты из запроса к БД
            Object newObject = instantiate(clazz);

            for (Map.Entry<String, String> entry : classFieldColumnNameMap.entrySet()) {
                String fieldName = entry.getKey();
                String columnName = entry.getValue();
                setFieldValue(newObject, fieldName, queryResultMap.get(columnName));
            }

            return (User) newObject;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void execQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        stmt.close();
    }

    public <T> T execQuery(String query, TResultHandler<T> handler) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        ResultSet result = stmt.getResultSet();
        T value = handler.handle(result);
        result.close();
        stmt.close();
        return value;
    }

}

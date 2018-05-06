package ru.nchernetsov.database_mapper.orm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DataSetDescriptor {
    private Map<String, String> columns = new HashMap<>();

    void put(String fieldName, String columnName) {
        columns.put(fieldName, columnName);
    }

    String get(String fieldName) {
        return columns.get(fieldName);
    }

    Collection<String> values() {
        return columns.values();
    }

    Set<Map.Entry<String, String>> entrySet() {
        return columns.entrySet();
    }
}

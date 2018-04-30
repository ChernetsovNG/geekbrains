package ru.nchernetsov.map_matching.interfaces;

import ru.nchernetsov.geometry.Pos3D;
import ru.nchernetsov.map_matching.MMRequest;
import ru.nchernetsov.map_matching.MMResult;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для получения результатов Map Matching'а для конкретного устройства
 */
public interface DeviceMMResults {
    boolean isEmpty();  // свежесозданный или есть история

    void addMMRequest(MMRequest mmRequest);  // добавить запрос на выполнение Map Matching'а
    void addMMResult(Optional<MMResult> mmResult);

    Optional<MMResult> getLastMMResult();     // получить последний результат Map Matching'а для устройства
    List<Optional<MMResult>> getMMResults();  // получить все сохранённые результаты Map Matching'а для устройства

    Optional<Pos3D> getLastPos3D();        // получить последнюю точку
    List<Optional<Pos3D>> getListPos3D();  // получить список точек

    MMRequest getLastMMRequest();
    List<MMRequest> getMMRequests();

    void clear();  // очистить сохранённые результаты
}
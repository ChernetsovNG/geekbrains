package ru.nchernetsov.map_matching.interfaces;

import java.util.Map;

/**
 * Интерфейс доступа к результатам Map Matching'а
 */
public interface DevicesMMResultsHolder {
    DeviceMMResults getByID(long deviceID);  // создаёт новый экземпляр DeviceMMResults, если его не существовало
    void save(long deviceID, DeviceMMResults deviceMMResults);
    Map<Long, DeviceMMResults> getDevicesMMResults();
    void setDevicesMMResults(Map<Long, DeviceMMResults> devicesMMResults);
    // очистить сохранённые результаты для всех устройств
    void clear();
}

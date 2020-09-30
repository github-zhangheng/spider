package com.zh.administrativedivision.service;

import com.zh.administrativedivision.module.Area;

import java.util.List;

public interface AreaService {
    List<Area> listAreas();

    void saveArea(Area area);

    void saveAreas(List<Area> areaList);
}

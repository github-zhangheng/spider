package com.zh.administrativedivision.service;

import com.zh.administrativedivision.module.Area;

import java.util.List;

/**
 * @author zhangheng
 * @since 2020/9/29 9:01
 */
public interface AreaService {
    List<Area> listAreas();

    void saveArea(Area area);

    void saveAreas(List<Area> areaList);
}
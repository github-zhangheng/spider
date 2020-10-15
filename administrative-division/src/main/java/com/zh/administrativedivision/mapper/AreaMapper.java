package com.zh.administrativedivision.mapper;

import com.zh.administrativedivision.module.Area;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhengheng
 */
@Mapper
public interface AreaMapper {
    List<Area> listAreas();

    void insertArea(Area area);

    void insertAreas(List<Area> areaList);
}
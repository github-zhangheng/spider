package com.zh.administrativedivision.service.impl;

import com.zh.administrativedivision.mapper.AreaMapper;
import com.zh.administrativedivision.module.Area;
import com.zh.administrativedivision.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangheng
 * @since 2020/9/29 9:02
 **/
@Service
public class AreaServiceImpl implements AreaService {

    @Resource
    private AreaMapper areaMapper;

    @Override
    public List<Area> listAreas() {
        return areaMapper.listAreas();
    }

    @Override
    public void saveArea(Area area) {
         areaMapper.insertArea(area);
    }

    @Override
    public void saveAreas(List<Area> areaList) {
         areaMapper.insertAreas(areaList);
    }
}
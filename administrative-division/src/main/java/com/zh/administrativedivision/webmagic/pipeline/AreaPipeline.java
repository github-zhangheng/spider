package com.zh.administrativedivision.webmagic.pipeline;

import com.zh.administrativedivision.mapper.AreaMapper;
import com.zh.administrativedivision.module.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * @author zhangheng
 * @since 2020/9/29 10:11
 **/
@Component
public class AreaPipeline implements Pipeline {
    @Autowired
    private AreaMapper areaMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Area> areaList = resultItems.get("areaList");
        if (areaList != null && areaList.size() > 0) {
            areaMapper.insertAreas(areaList);
        }
    }
}

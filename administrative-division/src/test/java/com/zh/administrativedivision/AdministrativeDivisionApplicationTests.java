package com.zh.administrativedivision;

import com.zh.administrativedivision.module.Area;
import com.zh.administrativedivision.service.AreaService;
import com.zh.administrativedivision.webmagic.pipeline.AreaPipeline;
import com.zh.administrativedivision.webmagic.processor.AreaProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class AdministrativeDivisionApplicationTests {
    @Autowired
    private AreaService areaService;
    @Autowired
    private AreaPipeline areaPipeline;

    @Test
    void listAreas() {
        List<Area> areaList = areaService.listAreas();
        System.out.println(areaList);
    }

    @Test
    void saveArea() {
        Area area = new Area();
        area.setAreaId("4");
        area.setAreaName("5");
        area.setAreaCode("6");
        area.setAreaLevel(1);

        areaService.saveArea(area);
    }

    @Test
    void saveAreas() {
        Area area1 = new Area();
        area1.setAreaId("7");
        area1.setAreaName("8");
        area1.setAreaCode("9");
        area1.setAreaLevel(1);

        Area area2 = new Area();
        area2.setAreaId("10");
        area2.setAreaName("11");
        area2.setAreaLevel(1);

        List<Area> areaList = new ArrayList<>();
        areaList.add(area1);
        areaList.add(area2);

        areaService.saveAreas(areaList);
    }

    @Test
    void saveProvinceAreas() {
        // 测试页面地址
        // String indexUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/21.html";
        // String indexUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/21/2101.html";
        // String indexUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/21/01/210102.html";
        // String indexUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/21/01/02/210102001.html";

        // 存在部分汉字乱码的页面地址（只列出一部分测试）
        // 该页面有个字“冮“（音：gāng），乱码
        // String indexUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/21/07/82/210782101.html";
        // 该页面有个字“牤“（音：māng），乱码
        String indexUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/21/07/81/210781112.html";


        // 2020年9月29日国家统计局行政区划代码2019年版首页地址
        // String indexUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/index.html";

        // 要爬取的省行政区划代码前两位
        String provinceAreaCode = "21";
        Spider.create(new AreaProcessor(indexUrl, provinceAreaCode))
                .addUrl(indexUrl)
                .setScheduler(new QueueScheduler())
                // 保存到数据库
                // .addPipeline(areaPipeline)
                .thread(1)
                .run();
    }

}

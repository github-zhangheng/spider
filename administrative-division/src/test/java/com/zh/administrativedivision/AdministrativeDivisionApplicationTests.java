package com.zh.administrativedivision;

import com.zh.administrativedivision.module.Area;
import com.zh.administrativedivision.service.AreaService;
import com.zh.administrativedivision.webmagic.pipeline.AreaPipeline;
import com.zh.administrativedivision.webmagic.processor.AreaProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class AdministrativeDivisionApplicationTests {
    @Resource
    private AreaService areaService;
    @Resource
    private AreaPipeline areaPipeline;

    @Test
    void listAreas() {
        List<Area> areaList = areaService.listAreas();
        System.out.println(areaList);
    }

    @Test
    void saveArea() {
        Area area = new Area();
        area.setAreaId("1");
        area.setAreaName("2");
        area.setAreaCode("3");
        area.setAreaParentCode("3_P");
        area.setAreaLevel(1);

        areaService.saveArea(area);
    }

    @Test
    void saveAreas() {
        Area area1 = new Area();
        area1.setAreaId("4");
        area1.setAreaName("5");
        area1.setAreaCode("6");
        area1.setAreaParentCode("6_P");
        area1.setAreaLevel(1);

        Area area2 = new Area();
        area2.setAreaId("7");
        area2.setAreaName("8");
        area1.setAreaCode("9");
        area1.setAreaParentCode("9_P");
        area2.setAreaLevel(1);

        List<Area> areaList = new ArrayList<>();
        areaList.add(area1);
        areaList.add(area2);

        areaService.saveAreas(areaList);
    }

    @Test
    void saveProvinceAreas() {
        // 数据均为2019年数据，测试三种情况
        // 1：爬取页为首页，则爬取全国各个地区
        // 2：爬取页为直辖市，则爬取该直辖市
        // 3：爬取页为省，则爬取该省
        // 区划编码首页
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/index.html";

        // 北京市
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/11.html";
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/11/1101.html";
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/11/01/110101.html";
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/11/01/01/110101001.html";

        // 河北省
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/13.html";
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/13/1301.html";
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/13/01/130102.html";
        // String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/13/01/02/130102001.html";

        // 需要爬取的区划编码
        String startUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/51/5107.html";

        Spider.create(new AreaProcessor("GB18030", startUrl))
                .addUrl(startUrl)
                .setScheduler(new QueueScheduler())
                // 保存到数据库
                .addPipeline(areaPipeline)
                .thread(5)
                .run();
    }
}
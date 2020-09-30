package com.zh.administrativedivision.webmagic.processor;

import cn.hutool.core.util.IdUtil;
import com.zh.administrativedivision.module.Area;
import com.zh.administrativedivision.type.AreaLevelEnum;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangheng
 * @since 2020/9/29 10:04
 **/
public class AreaProcessor implements PageProcessor {

    private final Site site = Site.me().setRetryTimes(20).setCycleRetryTimes(20).setSleepTime(1000).setTimeOut(20 * 1000);

    private final String provinceAreaCode;
    private final String preUrl;

    public AreaProcessor(String indexUrl, String provinceAreaCode) {
        this.provinceAreaCode = provinceAreaCode;
        this.preUrl = indexUrl.replace("index.html", provinceAreaCode).replaceAll("\\.", "\\\\.");
    }

    @Override
    public void process(Page page) {
        site.setCharset("utf8");
        List<Area> areaList = new ArrayList<>();

        Html currentHtml = page.getHtml();
        if (currentHtml.xpath("//table[@class='provincetable']").match()) {
            Selectable provinceSelectable = currentHtml.xpath("//a[@href='" + provinceAreaCode + ".html']/text()");

            Area area = new Area();
            area.setAreaId(IdUtil.simpleUUID());
            area.setAreaCode(setAreaValue(provinceAreaCode));
            area.setAreaName(provinceSelectable.toString());
            area.setAreaLevel(AreaLevelEnum.PROVINCE.getCode());

            areaList.add(area);
        } else {
            if (currentHtml.xpath("//table[@class='citytable']").match()) {
                setAreaValue(areaList, currentHtml, AreaLevelEnum.CITY);
            } else if (currentHtml.xpath("//table[@class='countytable']").match()) {
                setAreaValue(areaList, currentHtml, AreaLevelEnum.COUNTY);
            } else if (currentHtml.xpath("//table[@class='towntable']").match()) {
                setAreaValue(areaList, currentHtml, AreaLevelEnum.TOWN);
            } else if (currentHtml.xpath("//table[@class='villagetable']").match()) {
                setAreaValue(areaList, currentHtml, AreaLevelEnum.VILLAGE);
            }
        }

        if (areaList.size() > 0) {
            page.putField("areaList", areaList);
        }

        // 发现当前页面符合正则表达式的页面链接添加到队列中，等待下一轮爬取
        // page.addTargetRequests(page.getHtml().links().regex("(" + preUrl + "(/(\\d){1,12}){0,4}(21(\\d){2,12}){0,1}.html)").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    private void setAreaValue(List<Area> areaList, Html currentHtml, AreaLevelEnum areaLevelEnum) {
        String areaLevelName = areaLevelEnum.getName();
        List<Selectable> areaSelectableList = currentHtml.xpath("//table[@class='" + areaLevelName + "table']/tbody/tr[@class='" + areaLevelName + "tr']").nodes();

        for (Selectable areaSelectable : areaSelectableList) {
            Area area = new Area();
            area.setAreaId(IdUtil.simpleUUID());
            area.setAreaLevel(areaLevelEnum.getCode());

            if (areaSelectable.xpath("//tr/td[1]").match() && areaSelectable.xpath("//tr/td[1]/a").match()) {
                area.setAreaCode(setAreaValue(areaSelectable.xpath("//tr/td[1]/a/text()").toString()));
                area.setAreaName(areaSelectable.xpath("//tr/td[2]/a/text()").toString());
            } else if (areaSelectable.xpath("//tr/td[1]").match()) {
                area.setAreaCode(setAreaValue(areaSelectable.xpath("//tr/td[1]/text()").toString()));

                if (AreaLevelEnum.VILLAGE != areaLevelEnum) {
                    area.setAreaName(areaSelectable.xpath("//tr/td[2]/text()").toString());
                } else {
                    area.setAreaName(areaSelectable.xpath("//tr/td[3]/text()").toString());
                }
            }

            areaList.add(area);
        }
    }

    private String setAreaValue(String areaCode) {
        int areaCodeLength = areaCode.length();
        switch (areaCodeLength) {
            case 0:
                areaCode = areaCode + "000000000000";
                break;
            case 1:
                areaCode = areaCode + "00000000000";
                break;
            case 2:
                areaCode = areaCode + "0000000000";
                break;
            case 3:
                areaCode = areaCode + "000000000";
                break;
            case 4:
                areaCode = areaCode + "00000000";
                break;
            case 5:
                areaCode = areaCode + "0000000";
                break;
            case 6:
                areaCode = areaCode + "000000";
                break;
            case 7:
                areaCode = areaCode + "00000";
                break;
            case 8:
                areaCode = areaCode + "0000";
                break;
            case 9:
                areaCode = areaCode + "000";
                break;
            case 10:
                areaCode = areaCode + "00";
                break;
            case 11:
                areaCode = areaCode + "0";
                break;
            default:
                return areaCode;
        }
        return areaCode;
    }

}

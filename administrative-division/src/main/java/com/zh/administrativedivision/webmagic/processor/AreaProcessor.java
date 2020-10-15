package com.zh.administrativedivision.webmagic.processor;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.zh.administrativedivision.module.Area;
import com.zh.administrativedivision.type.AreaLevelEnum;
import com.zh.administrativedivision.util.DownloaderUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangheng
 * @since 2020/9/29 10:04
 **/
public class AreaProcessor implements PageProcessor {
    private final Site site = Site.me().setRetryTimes(20).setCycleRetryTimes(20).setSleepTime(1000).setTimeOut(20 * 1000);

    private final String charSet;
    private final String startUrl;

    public AreaProcessor(String charSet, String startUrl) {
        this.site.setCharset(charSet);
        this.charSet = charSet;
        this.startUrl = startUrl;
    }

    @Override
    public void process(Page page) {
        String currentPageUrl = page.getUrl().toString();
        Html currentHtml = page.getHtml();
        List<Area> areaList = new ArrayList<>();
        if (currentHtml.xpath("//table[@class='provincetable']").match()) {
            getAreaData(currentPageUrl, currentHtml, AreaLevelEnum.PROVINCE, areaList);
        } else if (currentHtml.xpath("//table[@class='citytable']").match()) {
            getAreaData(currentPageUrl, currentHtml, AreaLevelEnum.CITY, areaList);
        } else if (currentHtml.xpath("//table[@class='countytable']").match()) {
            getAreaData(currentPageUrl, currentHtml, AreaLevelEnum.COUNTY, areaList);
        } else if (currentHtml.xpath("//table[@class='towntable']").match()) {
            getAreaData(currentPageUrl, currentHtml, AreaLevelEnum.TOWN, areaList);
        } else if (currentHtml.xpath("//table[@class='villagetable']").match()) {
            getAreaData(currentPageUrl, currentHtml, AreaLevelEnum.VILLAGE, areaList);
        }

        if (areaList.size() > 0) {
            page.putField("areaList", areaList);
        }

        // 发现当前页面符合正则表达式的页面链接添加到队列中，等待下一轮爬取
        page.addTargetRequests(page.getHtml().links().regex("(http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/(\\d){4}(/(\\d){1,12}){0,4}/(\\d){0,12}.html)").all());
        System.out.println("pause");
    }

    @Override
    public Site getSite() {
        return site;
    }

    private void getAreaData(String currentPageUrl, Html currentHtml, AreaLevelEnum areaLevelEnum, List<Area> areaList) {
        String areaLevelName = areaLevelEnum.getName();
        String targetXpath = "//table[@class='" + areaLevelName + "table']/tbody/tr[@class='" + areaLevelName + "tr']";
        String areaParentCode = null;
        if (areaLevelEnum == AreaLevelEnum.PROVINCE) {
            targetXpath = targetXpath + "/td/a";
        } else {
            if (this.startUrl.equals(currentPageUrl)) {
                Area area = new Area();
                area.setAreaId(IdUtil.simpleUUID());

                String areaParentPageUrlPrefix = ReUtil.delAll("/(\\d){0,12}.html", currentPageUrl);
                String areaParentPageUrl;
                if (areaLevelEnum == AreaLevelEnum.CITY) {
                    areaParentPageUrl = areaParentPageUrlPrefix + "/index.html";
                } else {
                    area.setAreaParentCode(getCompleteAreaCode(ReUtil.delAll("/(\\d){0,12}.html", ReUtil.get("(\\d){0,12}/(\\d){0,12}.html", currentPageUrl, 0))));

                    areaParentPageUrl = areaParentPageUrlPrefix + ".html";
                }

                AreaLevelEnum areaParentEnum = Objects.requireNonNull(getAreaParentLevelEnum(areaLevelEnum));
                area.setAreaLevel(areaParentEnum.getCode());

                String areaParentEnumName = areaParentEnum.getName();
                Html areaParentHtml = DownloaderUtils.getPage(areaParentPageUrl, charSet);
                if (areaLevelEnum == AreaLevelEnum.CITY) {
                    Selectable areaParentSelectable = areaParentHtml.xpath("//table[@class='" + areaParentEnumName + "table']/tbody/tr[@class='" + areaParentEnumName + "tr']/td/a[@href='" + ReUtil.get("(\\d){0,12}.html", currentPageUrl, 0) + "']");
                    area.setAreaCode(getCompleteAreaCode(areaParentSelectable.xpath("//a/@href").toString().replace(".html", "")));
                    area.setAreaName(areaParentSelectable.xpath("//a/text()").toString());
                } else {
                    List<Selectable> areaParentSelectableList = areaParentHtml.xpath("//table[@class='" + areaParentEnumName + "table']/tbody/tr[@class='" + areaParentEnumName + "tr']/td/a[@href='" + ReUtil.get("(\\d){0,12}/(\\d){0,12}.html", currentPageUrl, 0) + "']").nodes();
                    area.setAreaCode(getCompleteAreaCode(areaParentSelectableList.get(0).xpath("//a/text()").toString()));
                    area.setAreaName(areaParentSelectableList.get(1).xpath("//a/text()").toString());
                }

                areaList.add(area);
            }

            areaParentCode = getCompleteAreaCode(ReUtil.get("(\\d){0,12}.html", currentPageUrl, 0).replace(".html", ""));
        }

        List<Selectable> areaSelectableList = currentHtml.xpath(targetXpath).nodes();
        for (Selectable areaSelectable : areaSelectableList) {
            Area area = new Area();
            area.setAreaId(IdUtil.simpleUUID());
            area.setAreaLevel(areaLevelEnum.getCode());
            area.setAreaParentCode(areaParentCode);

            if (areaLevelEnum == AreaLevelEnum.PROVINCE) {
                area.setAreaCode(getCompleteAreaCode(ReUtil.delAll(".html", areaSelectable.xpath("//a/@href").toString())));
                area.setAreaName(areaSelectable.xpath("//a/text()").toString());
            } else if (areaLevelEnum != AreaLevelEnum.VILLAGE) {
                if(areaSelectable.xpath("//tr/td[1]/a").match()){
                    area.setAreaCode(getCompleteAreaCode(areaSelectable.xpath("//tr/td[1]/a/text()").toString()));
                    area.setAreaName(areaSelectable.xpath("//tr/td[2]/a/text()").toString());
                }else{
                    area.setAreaCode(getCompleteAreaCode(areaSelectable.xpath("//tr/td[1]/text()").toString()));
                    area.setAreaName(areaSelectable.xpath("//tr/td[2]/text()").toString());
                }
            } else {
                area.setAreaCode(getCompleteAreaCode(areaSelectable.xpath("//tr/td[1]/text()").toString()));
                area.setAreaName(areaSelectable.xpath("//tr/td[3]/text()").toString());
            }

            areaList.add(area);
        }
    }

    private AreaLevelEnum getAreaParentLevelEnum(AreaLevelEnum areaLevelEnum) {
        if (AreaLevelEnum.CITY == areaLevelEnum) {
            return AreaLevelEnum.PROVINCE;
        } else if (AreaLevelEnum.COUNTY == areaLevelEnum) {
            return AreaLevelEnum.CITY;
        } else if (AreaLevelEnum.TOWN == areaLevelEnum) {
            return AreaLevelEnum.COUNTY;
        } else if (AreaLevelEnum.VILLAGE == areaLevelEnum) {
            return AreaLevelEnum.TOWN;
        }

        return null;
    }

    private String getCompleteAreaCode(String areaCode) {
        if (areaCode == null) {
            return null;
        }

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
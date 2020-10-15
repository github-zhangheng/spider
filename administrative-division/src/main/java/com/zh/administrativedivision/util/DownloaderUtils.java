package com.zh.administrativedivision.util;

import us.codecraft.webmagic.selector.Html;

/**
 * @author zhangheng
 * @since 2020/10/14 22:55
 **/
public class DownloaderUtils {
    public static Html getPage(String url, String charsetName) {
        return new Html(RestTemplateUtils.getRestTemplate(charsetName).getForEntity(url, String.class).getBody());
    }
}
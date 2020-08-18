package com.xlj.esspider.util;

import com.xlj.esspider.pojo.PageContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Jsoup爬虫工具类
 * @Author XLJ
 * @Date 2020/8/11
 */

@Component
public class HtmlParseUtil {

    public List<PageContent> parsePage(String keyword) throws IOException {
        // 获取页面请求URL
        String url = "https://search.jd.com/Search?keyword=java" + keyword;
        // 解析网页，设置10s的超时时间
        Document document = Jsoup.parse(new URL(url), 10000);
        // 通过id和tag获取页面中的内容
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");

        List<PageContent> pageContents = new ArrayList<>();
        for (Element el : elements) {
            // 获取页面中的名称、图片、价格（图片懒加载地址）
            String name = el.getElementsByClass("p-name").eq(0).text();
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();

            // 将获取到的内容添加到list中
            PageContent pageContent = new PageContent();
            pageContent.setImg(img);
            pageContent.setPrice(price);
            pageContent.setName(name);
            pageContents.add(pageContent);


        }
        return pageContents;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new HtmlParseUtil().parsePage("java"));
    }
}

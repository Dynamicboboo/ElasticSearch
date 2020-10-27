package com.niu.utils;

import com.niu.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description ：
 * @Author tj
 * @Date 2020/10/26
 */
@Component
public class HtmlParseUtil {
    public List<Content> parseJD(String keywords) throws IOException {
        //获取资源 https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword="+keywords;
        //解析网页(Jsoup返回的document就是浏览器Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");
        ArrayList<Content> goodslist = new ArrayList<>();
        //获取元素中的内容，这里的el 就是每个li标签
        for (Element el : elements) {
            //不显示图片是因为 网站图片是懒加载 等页面渲染了 才会将图片显示出来
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
//            System.out.println("==================================");
//            System.out.println(img);
//            System.out.println(price);
//            System.out.println(title);
            Content content = new Content();
            content.setImg(img);
            content.setTitle(title);
            content.setPrice(price);
            goodslist.add(content);
        }
        return goodslist;
    }

}

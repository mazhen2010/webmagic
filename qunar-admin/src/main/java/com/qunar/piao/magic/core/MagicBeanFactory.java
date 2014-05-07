package com.qunar.piao.magic.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Spider;

import java.util.Map;

/**
 * User: zhen.ma
 * Date: 14-4-25
 * Time: 下午12:39
 */
public class MagicBeanFactory {

    private static final String SPIDER_XML_PATH = "classpath:spiders/*spider.xml";

    public static <T> T getBean(String name, Class<T> requiredType) {
        ApplicationContext factory = new ClassPathXmlApplicationContext(SPIDER_XML_PATH);
        return factory.getBean(name, requiredType);
    }

    public static Map<String, Spider> getAllSpider() {
        ApplicationContext factory = new ClassPathXmlApplicationContext(SPIDER_XML_PATH);
        return factory.getBeansOfType(Spider.class);
    }

    public static void main(String[] args) {
        Map<String, Spider> spiderMap = MagicBeanFactory.getAllSpider();
        for (Map.Entry<String, Spider> entry : spiderMap.entrySet()) {
            entry.getValue().run();
        }
    }
}

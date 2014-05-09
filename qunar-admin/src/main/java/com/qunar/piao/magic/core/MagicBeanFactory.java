package com.qunar.piao.magic.core;

import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * User: zhen.ma
 * Date: 14-4-25
 * Time: 下午12:39
 */
@Service("magicBeanFactory")
public class MagicBeanFactory {

    private static final String SPIDER_XML_PATH = "classpath:spider/*spider.xml";

    @Resource
    private Pipeline pgPipeline;

    public static <T> T getBean(String name, Class<T> requiredType) {
        ApplicationContext factory = new ClassPathXmlApplicationContext(SPIDER_XML_PATH);
        return factory.getBean(name, requiredType);
    }

    public Map<String, Spider> getAllSpider() {

        ApplicationContext factory = new ClassPathXmlApplicationContext(SPIDER_XML_PATH);
        Map<String, Spider> spiderMap = factory.getBeansOfType(Spider.class);
        for (Map.Entry<String, Spider> entry : spiderMap.entrySet()) {
            entry.getValue().setPipelines(Lists.newArrayList(pgPipeline));
        }
        return spiderMap;

    }

    public static void main(String[] args) {
        MagicBeanFactory factory = new MagicBeanFactory();
        Map<String, Spider> spiderMap = factory.getAllSpider();
        for (Map.Entry<String, Spider> entry : spiderMap.entrySet()) {
            entry.getValue().run();
        }
    }
}

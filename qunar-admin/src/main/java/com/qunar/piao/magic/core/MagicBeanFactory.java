package com.qunar.piao.magic.core;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * User: zhen.ma
 * Date: 14-4-25
 * Time: 下午12:39
 */
public class MagicBeanFactory {

    private static final String SPIDER_XML_PATH = "classpath:spider/*spider.xml";

    @Resource
    private Pipeline pgPipeline;

    private List<String> proxyIpList;

    private List<String> userAgentList;

    private Map<String, String> headerMap = new HashMap<String, String>(8);

    public <T> T getBean(String name, Class<T> requiredType) {
        ApplicationContext factory = new ClassPathXmlApplicationContext(SPIDER_XML_PATH);
        return factory.getBean(name, requiredType);
    }

    public Map<String, Spider> getAllSpider() {

        ApplicationContext factory = new ClassPathXmlApplicationContext(SPIDER_XML_PATH);
        Map<String, Spider> spiderMap = factory.getBeansOfType(Spider.class);
        for (Map.Entry<String, Spider> entry : spiderMap.entrySet()) {
            setSpiderPipeline(entry.getValue());
            setSpiderHttpProxy(entry.getValue());
            setSpiderUserAgent(entry.getValue());
            setSpiderHeader(entry.getValue());
        }
        return spiderMap;

    }

    private void setSpiderPipeline(Spider spider) {
        if (spider == null || pgPipeline == null) {
            return;
        }

        spider.setPipelines(Lists.newArrayList(pgPipeline));
        return;
    }

    private void setSpiderHttpProxy(Spider spider) {
        if (spider == null || CollectionUtils.isEmpty(proxyIpList)) {
            return;
        }
        spider.getSite().setProxyIpList(proxyIpList);
    }

    private void setSpiderUserAgent(Spider spider) {
        if (spider == null || CollectionUtils.isEmpty(userAgentList)) {
            return;
        }

        Random random = new Random();
        int i = random.nextInt(userAgentList.size());
        String userAgent = userAgentList.get(i);
        if (StringUtils.isEmpty(userAgent)) {
            return;
        }

        spider.getSite().setUserAgent(userAgent);
    }

    private void setSpiderHeader(Spider spider) {
        if (spider == null || MapUtils.isEmpty(headerMap)) {
            return;
        }
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            spider.getSite().addHeader(entry.getKey(), entry.getValue());
        }
    }

    public Pipeline getPgPipeline() {
        return pgPipeline;
    }

    public void setPgPipeline(Pipeline pgPipeline) {
        this.pgPipeline = pgPipeline;
    }

    public List<String> getProxyIpList() {
        return proxyIpList;
    }

    public void setProxyIpList(List<String> proxyIpList) {
        this.proxyIpList = proxyIpList;
    }

    public List<String> getUserAgentList() {
        return userAgentList;
    }

    public void setUserAgentList(List<String> userAgentList) {
        this.userAgentList = userAgentList;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public static void main(String[] args) {
        MagicBeanFactory factory = new MagicBeanFactory();
        Map<String, Spider> spiderMap = factory.getAllSpider();
        for (Map.Entry<String, Spider> entry : spiderMap.entrySet()) {
            entry.getValue().run();
        }
    }
}

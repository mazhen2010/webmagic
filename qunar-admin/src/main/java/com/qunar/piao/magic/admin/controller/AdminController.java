package com.qunar.piao.magic.admin.controller;

import com.qunar.piao.magic.core.MagicBeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.codecraft.webmagic.Spider;

import javax.annotation.Resource;
import java.util.Map;

/**
 * User: zhen.ma
 * Date: 14-5-8
 * Time: 下午6:14
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private MagicBeanFactory magicBeanFactory;

    @ResponseBody
    @RequestMapping("/test")
    public String test() {
        Map<String, Spider> spiderMap = magicBeanFactory.getAllSpider();
        for (Map.Entry<String, Spider> entry : spiderMap.entrySet()) {
            entry.getValue().run();
        }
        return "false";
    }
}

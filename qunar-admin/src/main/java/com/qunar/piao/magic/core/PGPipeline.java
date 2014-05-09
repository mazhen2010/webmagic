package com.qunar.piao.magic.core;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;

/**
 * User: zhen.ma
 * Date: 14-5-7
 * Time: 下午8:16
 */
@Repository("pgPipeline")
public class PGPipeline implements Pipeline {

    @Resource
    private JdbcTemplate spiderJdbcTemplate;

    private static final String insert_product_sql = "insert into spot_product(wrapper_id,source_id,name," +
            "spot_name,cpc_sight_name,cpc_sight_id,cpc_sight_city,intro,body,detail_url,title) values (?,?,?,?,?,?,?,?,?,?,?)";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(ResultItems resultItems, Task task) {

        if (StringUtils.isEmpty((String)(resultItems.get("wrapperId")))
                || StringUtils.isEmpty((String)(resultItems.get("sourceId")))) {
            logger.error("PGPipeline don't process.wrapperId={},sourceId={}",
                    resultItems.get("wrapperId"), resultItems.get("sourceId"));
            return;
        }
        deleteProduct(String.valueOf(resultItems.get("wrapperId")), String.valueOf(resultItems.get("sourceId")));
        saveProduct(resultItems);

    }

    private void deleteProduct(String wrapperId,String sourceId) {
        String del_product_sql = "delete from spot_product where wrapper_id='%s' and source_id='%s'";
        del_product_sql = String.format(del_product_sql, wrapperId,sourceId);
        this.spiderJdbcTemplate.execute(del_product_sql);
    }

    private void saveProduct(ResultItems resultItems) {
        spiderJdbcTemplate.update(insert_product_sql,
                new Object[]{String.valueOf(resultItems.get("wrapperId")), String.valueOf(resultItems.get("sourceId")),
                        String.valueOf(resultItems.get("productName")), String.valueOf(resultItems.get("productName")),
                        String.valueOf(resultItems.get("productName")), String.valueOf(resultItems.get("sightId")),
                        String.valueOf(resultItems.get("sightCity")), String.valueOf(resultItems.get("intro")),
                        JSONObject.toJSONString(resultItems.getAll()), resultItems.getRequest().getUrl(),
                        String.valueOf(resultItems.get("title"))});
    }


//    private void printToConsole(ResultItems resultItems, Task task) {
//        System.out.println("get page: " + resultItems.getRequest().getUrl());
//        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
//            System.out.println(entry.getKey() + ":\t" + entry.getValue());
//        }
//    }
}

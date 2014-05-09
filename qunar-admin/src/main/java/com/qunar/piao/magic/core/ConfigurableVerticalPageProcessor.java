package com.qunar.piao.magic.core;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * User: zhen.ma
 * Date: 14-4-25
 * Time: 下午8:35
 */
public class ConfigurableVerticalPageProcessor implements PageProcessor {

    private Site site;

    private String seedUrl;

    private List<VerticalExtractRule> extractRules;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(Page page) {
        for (VerticalExtractRule extractRule : extractRules) {
            if (!extractRule.isMatchUrl(page.getUrl().toString())) {
                continue;
            }

            if (extractRule.isMulti()) {
                processMultiResult(page, extractRule);
            } else {
                processSimpleResult(page, extractRule);
            }
        }

        setPageSkipPipeline(page);
        logger.info("ConfigurableVerticalPageProcessor process {}, target request size {}, field size {}, skip {}",
                        page.getUrl(), page.getTargetRequests().size(), page.getResultItems().getAll().size(),
                        page.getResultItems().isSkip());
    }

    private void processMultiResult(Page page, VerticalExtractRule extractRule) {
        List<String> resultList = page.getHtml().selectDocumentForList(extractRule.getSelectorList());
        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }
        if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.TARGET) ==0) {
            //test start 仅产生一个链接
//            String temp = resultList.get(0);
//            resultList.clear();
//            resultList.add(temp);
            //test end
            page.addTargetRequests(resultList);
        } else if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.ITEM) ==0) {
            page.putField(extractRule.getFieldName(), resultList);
        }
    }

    private void processSimpleResult(Page page, VerticalExtractRule extractRule) {
        String result = page.getHtml().selectDocument(extractRule.getSelectorList());
        if (StringUtils.isEmpty(result)) {
            return;
        }
        if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.TARGET) ==0) {

            List<String> processedResult = extractRule.processResult(result, page.getUrl().toString());
            if (!CollectionUtils.isEmpty(processedResult)) {
                //test start 仅产生一个链接
//                String temp = processedResult.get(0);
//                processedResult.clear();
//                processedResult.add(temp);
                //test end
                page.addTargetRequests(processedResult);
            } else {
                page.addTargetRequest(result);
            }
        } else if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.ITEM) ==0) {
            page.putField(extractRule.getFieldName(), result);
        }
    }

    private void setPageSkipPipeline(Page page) {
        if (MapUtils.isEmpty(page.getResultItems().getAll())) {
            page.setSkip(true);
            return;
        }
        page.putField("wrapperId", this.getSite().getDomain());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getSeedUrl() {
        return seedUrl;
    }

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
        if (site != null) {
            site.addStartUrl(seedUrl);
        }
    }

    public List<VerticalExtractRule> getExtractRules() {
        return extractRules;
    }

    public void setExtractRules(List<VerticalExtractRule> extractRules) {
        this.extractRules = extractRules;
    }

    public enum ExpressionParamType {

    }
}

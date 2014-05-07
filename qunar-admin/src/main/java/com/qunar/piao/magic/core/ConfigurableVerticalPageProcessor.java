package com.qunar.piao.magic.core;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

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

    @Override
    public void process(Page page) {
        for (VerticalExtractRule extractRule : extractRules) {
            if (extractRule.isMulti()) {
                List<String> resultList = page.getHtml().selectDocumentForList(extractRule.getSelectorList());
                if (extractRule.isNotNull() && CollectionUtils.isEmpty(resultList)) {
                    page.setSkip(true);
                    return;
                }
                if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.TARGET) ==0) {
                    //test start
                    String temp = resultList.get(0);
                    resultList.clear();
                    resultList.add(temp);
                    //test end
                    page.addTargetRequests(resultList);
                } else if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.ITEM) ==0) {
                    page.putField(extractRule.getFieldName(), resultList);
                }
            } else {
                String result = page.getHtml().selectDocument(extractRule.getSelectorList());
                if (extractRule.isNotNull() && result == null) {
                    page.setSkip(true);
                    return;
                }
                if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.TARGET) ==0) {

                    List<String> processedResult = extractRule.processResult(result, page.getUrl().toString());
                    if (!CollectionUtils.isEmpty(processedResult)) {
                        //test start
                        String temp = processedResult.get(0);
                        processedResult.clear();
                        processedResult.add(temp);
                        //test end
                        page.addTargetRequests(processedResult);
                    } else {
                        page.addTargetRequest(result);
                    }
                } else if (extractRule.getFieldType().compareTo(VerticalExtractRule.ExtractFieldType.ITEM) ==0) {
                    page.putField(extractRule.getFieldName(), result);
                }
            }
        }
        //test
        System.out.print(page.getResultItems());
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

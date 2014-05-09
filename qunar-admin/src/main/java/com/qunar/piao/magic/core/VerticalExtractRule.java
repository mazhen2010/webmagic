package com.qunar.piao.magic.core;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.configurable.ExpressionType;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.Selector;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static us.codecraft.webmagic.selector.Selectors.$;
import static us.codecraft.webmagic.selector.Selectors.regex;
import static us.codecraft.webmagic.selector.Selectors.xpath;

/**
 * User: zhen.ma
 * Date: 14-5-5
 * Time: 下午12:14
 */
public class VerticalExtractRule {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String fieldName;

    private ExtractFieldType fieldType = ExtractFieldType.ITEM;

    private ExpressionType expressionType;

    private String expressionValue;

    private List<String> expressionParams;

    private List<String> resultParams;

    private boolean multi = false;          //多匹配结果标志

    private volatile List<Selector> selectorList;

    private String matchUrlRegex;

    private boolean notNull = false;        //不可空字段为空时，跳过page持久化

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public String getExpressionValue() {
        return expressionValue;
    }

    public void setExpressionValue(String expressionValue) {
        this.expressionValue = expressionValue;
    }

    public ExtractFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(ExtractFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public List<String> getExpressionParams() {
        return expressionParams;
    }

    public void setExpressionParams(List<String> expressionParams) {
        this.expressionParams = expressionParams;
    }

    public List<String> getResultParams() {
        return resultParams;
    }

    public void setResultParams(List<String> resultParams) {
        this.resultParams = resultParams;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public String getMatchUrlRegex() {
        return matchUrlRegex;
    }

    public void setMatchUrlRegex(String matchUrlRegex) {
        this.matchUrlRegex = matchUrlRegex;
    }

    public boolean isMatchUrl(String url) {
        if (StringUtils.isEmpty(matchUrlRegex)) {
            return true;
        }
        Pattern itemPattern = Pattern.compile(matchUrlRegex);
        Matcher m = itemPattern.matcher(url);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public List<Selector> getSelectorList() {

        if (selectorList != null) {
            return selectorList;
        }

        synchronized (this) {
            if (selectorList != null) {
                return selectorList;
            }

            selectorList = Lists.newArrayList();
            if (!CollectionUtils.isEmpty(getExpressionParams())) {
                for (String param : getExpressionParams()) {
                    String[] splits = param.split(":");
                    if (splits.length > 1 && (param.length() > param.indexOf(":") + 1)) {
                        String paramValue = param.substring(param.indexOf(":") + 1);
                        Selector paramSelector = compileSelector(splits[0], paramValue);
                        if (paramSelector != null) {
                            selectorList.add(paramSelector);
                        }
                    }
                }
            }
            selectorList.add(compileSelector());
        }
        return selectorList;
    }

    private Selector compileSelector(String expressionType, String expressionValue) {
        if (ExpressionType.XPath.name().equals(expressionType)) {
            return xpath(expressionValue);
        } else if (ExpressionType.Regex.name().equals(expressionType)) {
            return regex(expressionValue);
        }
        return null;
    }

    private Selector compileSelector() {
        switch (expressionType) {
            case Css:
                return $(expressionValue);
            case XPath:
                return xpath(expressionValue);
            case Regex:
                return regex(expressionValue);
            case JsonPath:
                return new JsonPathSelector(expressionValue);
            default:
                return xpath(expressionValue);
        }
    }

    public List<String> processResult(String text, String pageUrl) {
        if (CollectionUtils.isEmpty(resultParams)) {
            return Lists.newArrayList(text);
        }

        List<String> resultList = Lists.newArrayList();
        for (String param : resultParams) {
            String[] splits = param.split(":");
            if (splits.length < 1 || (param.length() <= param.indexOf(":") + 1)) {
                continue;
            }
            String paramValue = param.substring(param.indexOf(":") + 1);
            if (splits[0].equals(ProcessResultType.SPLIT.name())) {
                resultList.addAll(SplitResult(text, paramValue));
            } else if (splits[0].equals(ProcessResultType.NEXT1.name())) {
                resultList.addAll(Next1(text, pageUrl, paramValue));
            }
        }
        return resultList;
    }

    private List<String> SplitResult(String text, String param) {
        if (StringUtils.isEmpty(param)) {
            return Lists.newArrayList();
        }
        String[] params = param.split("#");
        if (params.length != 2) {
            return Lists.newArrayList();
        }

        List<String> resultList = Lists.newArrayList();
        for (String aString : text.split(params[0].trim())) {
            resultList.add(String.format(params[1], aString));
        }
        return resultList;
    }

    private List<String> Next1(String text, String pageUrl, String param) {
        if (StringUtils.isEmpty(param)) {
            return Lists.newArrayList();
        }
        String[] params = param.split("#");
        if (params.length != 2) {
            return Lists.newArrayList();
        }

        int nextThreshold = parseString2Integer(params[1]);
        int countOfSplitText = text.split(params[0].trim()).length;
        if (countOfSplitText < nextThreshold) {
            return Lists.newArrayList();
        }

        if (pageUrl.indexOf("/page") > 0) {
            int pageNo = parseString2Integer(pageUrl.split("/page")[1]);
            pageNo++;
            return Lists.newArrayList(pageUrl.split("page")[0] + "/page" + pageNo);
        } else {
            return Lists.newArrayList(pageUrl + "/page2");
        }
    }

    private Integer parseString2Integer(String aString) {
        if (StringUtils.isEmpty(aString)) {
            return null;
        }

        Integer aInteger = null;
        try {
            aInteger = Integer.valueOf(aString);
        } catch (Exception ex) {
            logger.error("parseString2Integer error:" + ex.getMessage(), ex);
        }
        return aInteger;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public enum ExtractFieldType {
        ITEM, TARGET;
    }

    public enum ProcessResultType {
        SPLIT, NEXT1;
    }
}

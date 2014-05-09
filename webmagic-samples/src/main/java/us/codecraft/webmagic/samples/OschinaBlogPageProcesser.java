package us.codecraft.webmagic.samples;

import com.sun.org.apache.regexp.internal.recompile;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.util.List;
import javax.xml.xpath.*;

/**
 * @author code4crafter@gmail.com <br>
 */
public class OschinaBlogPageProcesser implements PageProcessor {

    private Site site = Site.me().setDomain("my.oschina.net").addStartUrl("http://www.meituan.com/deal/6624891.html");

    @Override
    public void process(Page page) {
        List<String> links = page.getHtml().links().regex("http://my\\.oschina\\.net/flashsword/blog/\\d+").all();
        page.addTargetRequests(links);
        page.putField("sight_name", page.getHtml().xpath("//div[@class='deal-component-container']/div[@class='deal-component-headline divider']/div[@class='sans-serif']/h1[@class='deal-component-title']/text()").toString());
        page.putField("sight_name_province", page.getHtml().xpath("//div[@class='deal-component-headline divider']/div[@class='sans-serif']/span[@class='deal-component-title-prefix']/text()").toString());
        page.putField("tags",page.getHtml().xpath("//div[@class='deal-component-container']/div[@class='deal-component-headline divider']/div[@class='sans-serif']/h1[@class='deal-component-title']/text()|//div[@class='deal-component-headline divider']/div[@class='sans-serif']/span[@class='deal-component-title-prefix']/text()").all().toArray().toString());

        page.putField("price", page.getHtml().xpath("//div[@class='deal-component-price']/h2[@class='deal-component-price-current sans-serif orange item']/strong/text()").toString());
        page.putField("origin_price", page.getHtml().xpath("//div[@class='deal-component-price']/del[@class='item']/text()").toString());
        page.putField("score", page.getHtml().xpath("//a[@class='look-normal']/span[@class='item']/span[@class='orange']/text()").toString());
        page.putField("comment_count", page.getHtml().xpath("//div[@class='deal-component-rating divider']/span[1]/a[3]/span[@class='vertical-divider item']/text()").toString());
        page.putField("amount", page.getHtml().xpath("//div[@class='deal-component-rating divider']/span[2]/span[@class='deal-component-rating-sold-count']/span[@class='orange']/strong/text()").toString());
        page.putField("period1", page.getHtml().xpath("//span[@class='deal-component-expiry-valid-through']/text()").toString());
        page.putField("period2", page.getHtml().xpath("//span[@class='deal-component-expiry-notice orange']/text()").toString());
        page.putField("period3", page.getHtml().xpath("//span[@class='deal-component-expiry-notice-detail']/text()").toString());


        page.putField("limitEnterInfo", page.getHtml().xpath("//div[@class='deal-component-opening-hours cf deal-component-inline-text-folded']/span[@class='deal-component-inline-text-container left']/span[@class='deal-component-inline-text-wrapper']/span/text()").toString());
        page.putField("anytime_refund", page.getHtml().xpath("//div[@class='deal-component-commitments divider']/a[@class='anytime-money-back item']/text()").toString());
        page.putField("overtime_refund", page.getHtml().xpath("//div[@class='deal-component-commitments divider']/a[@class='expiring-money-back item']/text()").toString());

        page.putField("bookInfo", page.getHtml().xpath("//div[@class='deal-term']").toString());
//        page.putField("sight_name", page.getHtml().xpath("concat(//div[@class='deal-component-headline divider']/div[@class='sans-serif']/span[@class='deal-component-title-prefix']/text(),//div[@class='deal-component-container']/div[@class='deal-component-headline divider']/div[@class='sans-serif']/h1[@class='deal-component-title']/text())").toString());
//        page.putField("sight_name_province", page.getHtml().xpath("//div[@class='deal-component-headline divider']/div[@class='sans-serif']/span[@class='deal-component-title-prefix']/text()").toString());
//        page.putField("tags",page.getHtml().xpath("//div[@class='deal-component-container']/div[@class='deal-component-headline divider']/div[@class='sans-serif']/h1[@class='deal-component-title']/text()|//div[@class='deal-component-headline divider']/div[@class='sans-serif']/span[@class='deal-component-title-prefix']/text()").all());
//
//        page.putField("price", page.getHtml().xpath("//div[@class='deal-component-price']/h2[@class='deal-component-price-current sans-serif orange item']/strong/text()").toString());
//        page.putField("origin_price", page.getHtml().xpath("substring-after(//div[@class='deal-component-price']/del[@class='item']/text(),\"¥\")").toString());
//        page.putField("score", page.getHtml().xpath("//a[@class='look-normal']/span[@class='item']/span[@class='orange']/text()").toString());
//        page.putField("commentCount", page.getHtml().xpath("substring-before(//div[@class='deal-component-rating divider']/span[1]/a[3]/span[@class='vertical-divider item']/text(),'人')").toString());
//        page.putField("amount", page.getHtml().xpath("//div[@class='deal-component-rating divider']/span[2]/span[@class='deal-component-rating-sold-count']/span[@class='orange']/strong/text()").toString());
//        page.putField("period", page.getHtml().xpath("concat(//span[@class='deal-component-expiry-valid-through']/text(),//span[@class='deal-component-expiry-notice orange']/text(),//span[@class='deal-component-expiry-notice-detail']/text())").toString());
//        page.putField("limitEnterInfo", page.getHtml().xpath("//div[@class='deal-component-opening-hours cf deal-component-inline-text-folded']/span[@class='deal-component-inline-text-container left']/span[@class='deal-component-inline-text-wrapper']/span/text()").toString());
//        page.putField("service", page.getHtml().xpath("concat(//div[@class='deal-component-commitments divider']/a[@class='anytime-money-back item']/text(),'#',//div[@class='deal-component-commitments divider']/a[@class='expiring-money-back item']/text(),'#',//div[@class='deal-component-commitments divider']/a[@class='real-comment item']/text())").toString());
//        page.putField("bookInfo", page.getHtml().xpath("//div[@class='deal-term']").toString());


        XPathFactory xFactory = XPathFactory.newInstance();
        XPath xpath = xFactory.newXPath();
        try {
            XPathExpression expr = xpath.compile("/html[@class='yui3-js-enabled']/body[@id='deal-default']/div[@id='doc']/div[@id='bdw']/div[@id='bd']/div[@class='deal-component-container']/div[@class='deal-component-headline divider']/div[@class='sans-serif']");
            Object result = expr.evaluate(page.getHtml().getDocument(), XPathConstants.NODESET);
            System.out.print(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void parseXPath(String text) {
        try {
            HtmlCleaner hc = new HtmlCleaner();
            TagNode tn = hc.clean(text);
            Object[] objarr = null;
            objarr = tn.evaluateXPath("substring-after(//div[@class='deal-component-price']/del[@class='item']/text(),\"¥\")");

            if (objarr != null && objarr.length > 0) {
                for (Object obj : objarr) {
                    TagNode tntr = (TagNode) obj;
                    String xptr = "//td[@align='left']//a";
                    Object[] objarrtr = null;
                    objarrtr = tntr.evaluateXPath(xptr);

                    if (objarrtr != null && objarrtr.length > 0) {
                        for (Object obja : objarrtr) {
                            TagNode tna = (TagNode) obja;
                            String str = tna.getText().toString();
                            System.out.print(str);

                        }

                    }

                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
        Spider.create(new OschinaBlogPageProcesser()).run();
    }
}

package org.apdplat.extractor.html.demo;

/**
 * Created by liuhuanhuan on 2017/4/8.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apdplat.extractor.html.HtmlExtractor;
import org.apdplat.extractor.html.HtmlFetcher;
import org.apdplat.extractor.html.impl.DefaultHtmlExtractor;
import org.apdplat.extractor.html.impl.ExtractRegular;
import org.apdplat.extractor.html.impl.JSoupHtmlFetcher;
import org.apdplat.extractor.html.model.CssPath;
import org.apdplat.extractor.html.model.ExtractFailLog;
import org.apdplat.extractor.html.model.ExtractResult;
import org.apdplat.extractor.html.model.ExtractResultItem;
import org.apdplat.extractor.html.model.HtmlTemplate;
import org.apdplat.extractor.html.model.UrlPattern;

/**
 *
 * @author 杨尚川
 */
public class NeteasyNewsExtractor {
    public static void main(String[] args) {
        //1、构造抽取规则
        List<UrlPattern> urlPatterns = new ArrayList<>();
        //1.1、构造URL模式
        UrlPattern urlPattern = new UrlPattern();
        urlPattern.setUrlPattern("http://money.163.com/\\d{2}/\\d{4}/\\d{2}/[0-9A-Z]{16}.html");
        //1.2、构造HTML模板
        HtmlTemplate htmlTemplate = new HtmlTemplate();
        htmlTemplate.setTemplateName("网易财经频道");
        htmlTemplate.setTableName("finance");
        //1.3、将URL模式和HTML模板建立关联
        urlPattern.addHtmlTemplate(htmlTemplate);
        //1.4、构造CSS路径
        CssPath cssPath = new CssPath();
        cssPath.setCssPath("h1");
        cssPath.setFieldName("title");
        cssPath.setFieldDescription("标题");
        //1.5、将CSS路径和模板建立关联
        htmlTemplate.addCssPath(cssPath);
        //1.6、构造CSS路径
        cssPath = new CssPath();
        cssPath.setCssPath("div#endText");
        cssPath.setFieldName("content");
        cssPath.setFieldDescription("正文");
        //1.7、将CSS路径和模板建立关联
        htmlTemplate.addCssPath(cssPath);
        //可象上面那样构造多个URLURL模式
        urlPatterns.add(urlPattern);
        //2、获取抽取规则对象
        ExtractRegular extractRegular = ExtractRegular.getInstance(urlPatterns);
        //注意：可通过如下3个方法动态地改变抽取规则
        //extractRegular.addUrlPatterns(urlPatterns);
        //extractRegular.addUrlPattern(urlPattern);
        //extractRegular.removeUrlPattern(urlPattern.getUrlPattern());
        //3、获取HTML抽取工具
        HtmlExtractor htmlExtractor = new DefaultHtmlExtractor(extractRegular);
        //4、抽取网页
        String url = "http://money.163.com/08/1219/16/4THR2TMP002533QK.html";
        HtmlFetcher htmlFetcher = new JSoupHtmlFetcher();
        String html = htmlFetcher.fetch(url);
        List<ExtractResult> extractResults = htmlExtractor.extract(url, html);
        //5、输出结果
        int i = 1;
        for (ExtractResult extractResult : extractResults) {
            System.out.println((i++) + "、网页 " + extractResult.getUrl() + " 的抽取结果");
            if(!extractResult.isSuccess()){
                System.out.println("抽取失败：");
                for(ExtractFailLog extractFailLog : extractResult.getExtractFailLogs()){
                    System.out.println("\turl:"+extractFailLog.getUrl());
                    System.out.println("\turlPattern:"+extractFailLog.getUrlPattern());
                    System.out.println("\ttemplateName:"+extractFailLog.getTemplateName());
                    System.out.println("\tfieldName:"+extractFailLog.getFieldName());
                    System.out.println("\tfieldDescription:"+extractFailLog.getFieldDescription());
                    System.out.println("\tcssPath:"+extractFailLog.getCssPath());
                    if(extractFailLog.getExtractExpression()!=null) {
                        System.out.println("\textractExpression:" + extractFailLog.getExtractExpression());
                    }
                }
                continue;
            }
            Map<String, List<ExtractResultItem>> extractResultItems = extractResult.getExtractResultItems();
            for(String field : extractResultItems.keySet()){
                List<ExtractResultItem> values = extractResultItems.get(field);
                if(values.size() > 1){
                    int j=1;
                    System.out.println("\t多值字段:"+field);
                    for(ExtractResultItem item : values){
                        System.out.println("\t\t"+(j++)+"、"+field+" = "+item.getValue());
                    }
                }else{
                    System.out.println("\t"+field+" = "+values.get(0).getValue());
                }
            }
            System.out.println("\tdescription = "+extractResult.getDescription());
            System.out.println("\tkeywords = "+extractResult.getKeywords());
        }
    }
}

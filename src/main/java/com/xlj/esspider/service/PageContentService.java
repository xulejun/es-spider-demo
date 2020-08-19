package com.xlj.esspider.service;

import com.alibaba.fastjson.JSON;
import com.xlj.esspider.pojo.PageContent;
import com.xlj.esspider.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author XLJ
 * @Date 2020/8/12
 */

@Service
public class PageContentService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * @description: 解析添加数据
     * @author XLJ
     * @date 2020/8/12 17:41
     */
    public Boolean parseContent(String keyword) throws IOException {
        // 页面解析
        List<PageContent> pageContents = new HtmlParseUtil().parsePage(keyword);
        // 创建ES数据处理对象
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        for (int i = 0; i < pageContents.size(); i++) {
            // 封装成json对象
            String jsonString = JSON.toJSONString(pageContents.get(i));
            // 将数据添加到ES中
            IndexRequest jd_goods = new IndexRequest("jd_goods").source(jsonString, XContentType.JSON);
            bulkRequest.add(jd_goods);

//            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(pageContents.get(i)), XContentType.JSON));
        }
        // ES执行添加数据
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        // 判断是否成功，没有失败，则返回成功
        return !bulk.hasFailures();
    }

    /**
     * @description: 实现搜索功能
     * @author XLJ
     * @date 2020/8/12 19:15
     */
    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        // 精确匹配——第一个参数为ES数据库中的字段
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        List<Map<String, Object>> lists = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            lists.add(documentFields.getSourceAsMap());
        }
        return lists;
    }

    /**
     * @description: 搜索结果显示高亮
     * @author XLJ
     * @date 2020/8/13 11:50
     */
    public List<Map<String, Object>> searchHighLight(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        // 精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        // 关闭多个字段匹配
        highlightBuilder.requireFieldMatch(false);
        // 将高亮放入搜索
        searchSourceBuilder.highlighter(highlightBuilder);

        // 执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        List<Map<String, Object>> lists = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            // 高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            // 原来的结果
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            // 解析高亮字段，替换成原来的结果
            if (name != null) {
                Text[] fragments = name.fragments();
                String new_title = "";
                for (Text fragment : fragments) {
                    new_title += fragment;
                }
                sourceAsMap.put("name", new_title);
            }
            lists.add(sourceAsMap);
        }
        return lists;
    }


}

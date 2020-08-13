package com.xlj.esspider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.xlj.esspider.pojo.PageContent;
import com.xlj.esspider.util.HtmlParseUtil;
import org.apache.lucene.util.QueryBuilder;
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
        // 将数据添加到ES中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        for (int i = 0; i < pageContents.size(); i++) {
            String jsonString = JSON.toJSONString(pageContents.get(i), (SerializeFilter) XContentType.JSON);
            IndexRequest jd_goods = new IndexRequest("jd_goods").source(jsonString);
            bulkRequest.add(jd_goods);
        }
        // ES执行添加数据
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        // 判断是否成功
        boolean hasFailures = bulk.hasFailures();
        return hasFailures;
    }

    /**
     * @description: 实现搜索功能
     * @author XLJ
     * @date 2020/8/12 19:15
     */
    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        // 精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        List<Map<String,Object>> lists = new ArrayList<>();
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
        if (pageNo<=1){
            pageNo=1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        // 精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        // 关闭多个字段匹配
        highlightBuilder.requireFieldMatch(false);
        // 执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        List<Map<String,Object>> lists = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            // 高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            // 原来的结果
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            // 解析高亮字段，替换成原来的结果
            if (title!=null){
                Text[] fragments = title.fragments();
                String new_title="";
                for (Text fragment : fragments) {
                    new_title += fragment;
                }
                sourceAsMap.put("title",new_title);
            }
            lists.add(sourceAsMap);
        }
        return lists;
    }


}

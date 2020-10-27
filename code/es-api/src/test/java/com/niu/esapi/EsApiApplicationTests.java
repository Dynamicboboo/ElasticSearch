package com.niu.esapi;


import com.alibaba.fastjson.JSON;
import com.niu.esapi.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //测试索引的创建  request
    @Test
    void testCreateIndex() throws IOException {
        //1、创建索引
        CreateIndexRequest request = new CreateIndexRequest("java_index");
        //2、执行请求
        CreateIndexResponse createIndexRequest = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexRequest);
    }
    //测试获取索引
    @Test
    void  testExistIndex() throws IOException {
        GetIndexRequest re = new GetIndexRequest("java_index");
        boolean exists = restHighLevelClient.indices().exists(re, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    //测试删除索引
    @Test
    void delIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("java_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    //添加文档
    @Test
    void testDocument() throws IOException {
        //创建对象
        User user = new User("牛牛", 3);
        //创建请求
        IndexRequest request = new IndexRequest("java_index");
        //规则 put/java_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        //将我们的数据放入请求 json
        IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求,获取响应的结果
        IndexResponse index = restHighLevelClient.index(source, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());
    }

    @Test
    void testExistDocument() throws IOException {
        //测试文档的 没有index
        GetRequest request= new GetRequest("java_index","1");
        //没有indices()了
        boolean exist = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println("测试文档是否存在-----"+exist);
    }

    //测试获取文档
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("java_index", "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        System.out.println("测试获取文档-----" + response.getSourceAsString());
        System.out.println("测试获取文档-----" + response);
    }
    /**
     * 结果：
     * 测试获取文档-----{"age":3,"name":"牛牛"}
     * 测试获取文档-----{"_index":"java_index","_type":"_doc","_id":"1","_version":1,"_seq_no":0,"_primary_term":1,"found":true,"_source":{"age":3,"name":"牛牛"}}
     */
    @Test
    void testUpdateDocument() throws IOException {
        User user = new User("李逍遥", 55);
        //修改是id为1的
        UpdateRequest request = new UpdateRequest("java_index", "1");
        request.timeout("1s");
        request.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println("测试修改文档-----" + response);
        System.out.println("测试修改文档-----" + response.status());
        /**
         * 结果
         * 测试修改文档-----UpdateResponse[index=java_index,type=_doc,id=1,version=2,seqNo=1,primaryTerm=1,result=updated,shards=ShardInfo{total=2, successful=1, failures=[]}]
         * 测试修改文档-----OK
         */
    }
    //测试删除文档
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request= new DeleteRequest("java_index","1");
        request.timeout("1s");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println("测试删除文档------"+response.status());
        /*
        测试删除文档------OK
         */
    }
    //测试批量添加文档
    @Test
    void testBulkAddDocument() throws IOException {
        ArrayList<User> userlist=new ArrayList<User>();
        userlist.add(new User("xn1",5));
        userlist.add(new User("xn2",6));
        userlist.add(new User("xn3",40));
        userlist.add(new User("xn4",25));
        userlist.add(new User("xn5",15));
        userlist.add(new User("xn6",35));

        //批量操作的Request
        BulkRequest request = new BulkRequest();
        request.timeout("1s");

        //批量处理请求
        for (int i = 0; i < userlist.size(); i++) {
            request.add(
                    new IndexRequest("java_index")
                            .id(""+(i+1))
                            .source(JSON.toJSONString(userlist.get(i)),XContentType.JSON)
            );
        }
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        //response.hasFailures()是否是失败的
        System.out.println("测试批量添加文档-----"+response.hasFailures());
//        结果:false为成功 true为失败
//        测试批量添加文档-----false
    }
    //测试查询文档

    //SearchRequest搜索请求
    //SearchsourceBuilder条件构造
    // HighLightBuilder构建高亮
    // TermQueryBuiLder精确查询
    // MatchALLQueryBuilder
    //xxx QueryBuiLdqr对应我们刚才看到的命令!

    @Test
    void testSearchDocument() throws IOException {
        SearchRequest request = new SearchRequest("java_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置了高亮
        sourceBuilder.highlighter();
        //term name为cyx1的
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "xn1");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("测试查询文档-----"+JSON.toJSONString(response.getHits()));
        System.out.println("=====================");
        for (SearchHit documentFields : response.getHits().getHits()) {
            System.out.println("测试查询文档--遍历参数--"+documentFields.getSourceAsMap());
        }
        /**
         * 测试查询文档-----{"fragment":true,"hits":[{"fields":{},"fragment":false,"highlightFields":{},"id":"1","matchedQueries":[],"primaryTerm":0,"rawSortValues":[],"score":1.540445,"seqNo":-2,"sortValues":[],"sourceAsMap":{"name":"xn1","age":5},"sourceAsString":"{\"age\":5,\"name\":\"xn1\"}","sourceRef":{"fragment":true},"type":"_doc","version":-1}],"maxScore":1.540445,"totalHits":{"relation":"EQUAL_TO","value":1}}
         * =====================
         * 测试查询文档--遍历参数--{name=xn1, age=5}
         * 2020-10-26 20:06:38.218  INFO 16292 --- [extShutdownHook] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
         */

    }



}

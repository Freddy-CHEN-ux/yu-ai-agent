package com.yupi.yuaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class PgVectorVectorStoreConfigTest {
    @Resource
    VectorStore pgVectorVectorStore;

    @Test
    public void test() {
        List<Document> documents = List.of(
                new Document("公子飞是一名大数据开发工程师，编码能力强，人也帅", Map.of("公子飞是谁", "公子飞")),
                new Document("公子飞是一名程序员，身高180，体重180",Map.of("公子飞身高多少", "身高")),
                new Document("公子飞的爱好是学习和爬山", Map.of("公子飞的爱好是什么", "爱好"))
        );
        // 添加文档
        pgVectorVectorStore.add(documents);
        // 相似度查询
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        Assertions.assertNotNull(results);
    }
}

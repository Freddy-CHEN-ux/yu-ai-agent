package com.yupi.yuaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

public class HttpAiInvoke {
    public static void main(String[] args) {
        // 替换为你的 API Key
        String dashscopeApiKey = TestApiKey.API_KEY;

        // 构建请求的 JSON 体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");

        // 构建 messages 数组
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");

        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");

        requestBody.set("messages", new JSONObject[]{systemMessage, userMessage});

        // 发送 POST 请求
        HttpResponse response = HttpRequest.post("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions")
                .header("Authorization", "Bearer " + dashscopeApiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute();

        // 输出响应结果
        System.out.println("Status Code: " + response.getStatus());
        System.out.println("Response Body: " + response.body());
    }
}


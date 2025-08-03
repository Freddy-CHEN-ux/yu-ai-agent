package com.yupi.yuaiagent.app;

import com.yupi.yuaiagent.advisor.ProhibitedWordsAdvisor;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是公子飞";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（公子）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下，回答我叫什么名字就好了";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员鱼皮，我想让另一半（编程导航）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        //使用Assertions输出debug错误日志
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void testProhibitedWordsDetection() {
        String chatId = UUID.randomUUID().toString();
        
        // 测试包含违禁词的输入 - 应该抛出异常
        String prohibitedMessage = "我想了解一些打炮的方法";
        
        ProhibitedWordsAdvisor.ProhibitedWordException exception = assertThrows(
            ProhibitedWordsAdvisor.ProhibitedWordException.class,
            () -> loveApp.doChat(prohibitedMessage, chatId),
            "应该检测到违禁词并抛出异常"
        );
        
        assertEquals("用户输入包含违禁词，请重新输入合适的内容。", exception.getMessage());
    }

    @Test
    void testProhibitedWordsCaseInsensitive() {
        String chatId = UUID.randomUUID().toString();
        
        // 测试大小写不敏感的违禁词检测
        String prohibitedMessage = "请告诉我关于打架的事情";
        
        ProhibitedWordsAdvisor.ProhibitedWordException exception = assertThrows(
            ProhibitedWordsAdvisor.ProhibitedWordException.class,
            () -> loveApp.doChat(prohibitedMessage, chatId),
            "应该检测到违禁词（大小写不敏感）并抛出异常"
        );
        
        assertEquals("用户输入包含违禁词，请重新输入合适的内容。", exception.getMessage());
    }

    @Test
    void testNormalInputWithoutProhibitedWords() {
        String chatId = UUID.randomUUID().toString();
        
        // 测试正常输入 - 不应该抛出异常
        String normalMessage = "你好，我想咨询一些恋爱建议";
        
        assertDoesNotThrow(() -> {
            String answer = loveApp.doChat(normalMessage, chatId);
            assertNotNull(answer, "正常输入应该返回有效回答");
        }, "正常输入不应该抛出异常");
    }

}

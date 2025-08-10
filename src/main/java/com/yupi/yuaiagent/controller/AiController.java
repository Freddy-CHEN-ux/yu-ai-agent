package com.yupi.yuaiagent.controller;

import com.yupi.yuaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "${cors.allowed-origins}", allowCredentials = "true")
public class AiController {
    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

//    @GetMapping("/love_app/chat/sync")
//    public String doChatWithLoveAppSync(String message, String chatId) {
//        return loveApp.doChat(message, chatId);
//    }

    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .doOnError(throwable -> {
                    // 记录错误但不中断流
                    if (throwable instanceof IOException) {
                        log.warn("SSE连接被客户端中断: chatId={}, message={}, error={}", 
                                chatId, message, throwable.getMessage());
                    } else {
                        log.error("SSE流处理异常: chatId={}, message={}", chatId, message, throwable);
                    }
                })
                .onErrorResume(throwable -> {
                    // 对于连接中断异常，返回空流而不是错误
                    if (throwable instanceof IOException && 
                        throwable.getMessage().contains("你的主机中的软件中止了一个已建立的连接")) {
                        log.info("客户端主动断开连接，正常结束流: chatId={}", chatId);
                        return Flux.empty();
                    }
                    // 其他异常返回错误信息
                    return Flux.just("发生错误，请重试");
                });
    }

//    @GetMapping("/love_app/chat/sse/emitter")
//    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
//        // 创建一个超时时间较长的 SseEmitter
//        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
//        // 获取 Flux 数据流并直接订阅
//        loveApp.doChatByStream(message, chatId)
//                .subscribe(
//                        // 处理每条消息
//                        chunk -> {
//                            try {
//                                emitter.send(chunk);
//                            } catch (IOException e) {
//                                emitter.completeWithError(e);
//                            }
//                        },
//                        // 处理错误
//                        emitter::completeWithError,
//                        // 处理完成
//                        emitter::complete
//                );
//        // 返回emitter
//        return emitter;
//    }


}

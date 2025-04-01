package com.aseubel.isee.controller;

import com.aseubel.isee.common.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final Map<String, SseEmitter> sseEmitters;

//    @RequirePermission(minAdminLevel = 1)
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String userId) {
        SseEmitter emitter = new SseEmitter(0L); // 无超时

        // 设置超时和完成时的回调，移除对应的发射器
        emitter.onTimeout(() -> sseEmitters.remove(userId));
        emitter.onCompletion(() -> sseEmitters.remove(userId));

        sseEmitters.put(userId, emitter);

        // 发送初始连接成功消息
        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected successfully"));
        } catch (IOException e) {
            emitter.complete();
            sseEmitters.remove(userId);
        }

        return emitter;
    }

//    @RequirePermission(minAdminLevel = 1)
    @DeleteMapping("/unsubscribe")
    public void unsubscribe(@RequestParam String userId) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter != null) {
            emitter.complete();
            sseEmitters.remove(userId);
        }
    }
}
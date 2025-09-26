package id.ysydev.kafka.sender.controller;

import id.ysydev.kafka.sender.kafka.MessagePayload;
import id.ysydev.kafka.sender.kafka.MessageProducer;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller sederhana untuk mem-publish pesan ke Kafka.
 * <p>
 * Endpoint:
 * - POST /api/messages
 * Body: { "text": "halo kafka" }
 * Respon: { status, id, topic }
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageProducer producer;

    public MessageController(MessageProducer producer) {
        this.producer = producer;
    }


    private String traceIdOrNew(@RequestHeader HttpHeaders headers) {
        String tid = headers.getFirst("X-Trace-Id");
        return (tid != null && !tid.isBlank()) ? tid : UUID.randomUUID().toString();
    }

    @PostMapping("/noretry")
    public Map<String, Object> toNoRetry(@RequestBody Map<String, String> body,
                                         @RequestHeader HttpHeaders headers) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        var traceId = traceIdOrNew(headers);
        producer.send("demo.noretry", payload, traceId);
        return Map.of("status", "PUBLISHED", "topic", "demo.noretry", "id", payload.id());
    }

    @PostMapping("/retry/simple")
    public Map<String, Object> toSimpleRetry(@RequestBody Map<String, String> body,
                                             @RequestHeader HttpHeaders headers) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        var traceId = traceIdOrNew(headers);
        producer.send("demo.retry.simple", payload,traceId);
        return Map.of("status", "PUBLISHED", "topic", "demo.retry.simple", "id", payload.id());
    }

    @PostMapping("/retry/staged")
    public Map<String, Object> toStagedRetry(@RequestBody Map<String, String> body,
                                             @RequestHeader HttpHeaders headers) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        var traceId = traceIdOrNew(headers);
        producer.send("demo.retry.staged",  payload,traceId);
        return Map.of("status", "PUBLISHED", "topic", "demo.retry.staged", "id", payload.id());
    }

    @PostMapping("/retry/exclude")
    public Map<String, Object> toExclude(@RequestBody Map<String, String> body,
                                         @RequestHeader HttpHeaders headers) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        var traceId = traceIdOrNew(headers);
        producer.send("demo.retry.exclude", payload,traceId);
        return Map.of("status", "PUBLISHED", "topic", "demo.retry.exclude", "id", payload.id());
    }

}

package id.ysydev.kafka.sender.controller;

import id.ysydev.kafka.sender.kafka.MessagePayload;
import id.ysydev.kafka.sender.kafka.MessageProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

    @PostMapping("/noretry")
    public Map<String, Object> toNoRetry(@RequestBody Map<String, String> body) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        producer.sendTo("demo.noretry", payload.id(), payload);
        return Map.of("status", "PUBLISHED", "topic", "demo.noretry", "id", payload.id());
    }

    @PostMapping("/retry/simple")
    public Map<String, Object> toSimpleRetry(@RequestBody Map<String, String> body) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        producer.sendTo("demo.retry.simple", payload.id(), payload);
        return Map.of("status", "PUBLISHED", "topic", "demo.retry.simple", "id", payload.id());
    }

    @PostMapping("/retry/staged")
    public Map<String, Object> toStagedRetry(@RequestBody Map<String, String> body) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        producer.sendTo("demo.retry.staged", payload.id(), payload);
        return Map.of("status", "PUBLISHED", "topic", "demo.retry.staged", "id", payload.id());
    }

    @PostMapping("/retry/exclude")
    public Map<String, Object> toExclude(@RequestBody Map<String, String> body) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(java.util.UUID.randomUUID().toString(), text, java.time.Instant.now());
        producer.sendTo("demo.retry.exclude", payload.id(), payload);
        return Map.of("status", "PUBLISHED", "topic", "demo.retry.exclude", "id", payload.id());
    }

}

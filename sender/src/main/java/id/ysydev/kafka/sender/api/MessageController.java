package id.ysydev.kafka.sender.api;

import id.ysydev.kafka.sender.kafka.MessageProducer;
import id.ysydev.kafka.sender.model.MessagePayload;
import id.ysydev.kafka.sender.model.NotificationPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller sederhana untuk mem-publish pesan ke Kafka.
 *
 * Endpoint:
 * - POST /api/messages
 *   Body: { "text": "halo kafka" }
 *   Respon: { status, id, topic }
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageProducer producer;

    public MessageController(MessageProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<?> send(@RequestBody Map<String, String> body) {
        var text = body.getOrDefault("text", "");
        var payload = new MessagePayload(UUID.randomUUID().toString(), text, Instant.now());
        producer.send(payload);
        return ResponseEntity.accepted().body(Map.of(
                "status", "PUBLISHED",
                "id", payload.id(),
                "topic", "demo.messages"
        ));
    }

    @PostMapping("/notify")
    public ResponseEntity<?> notify(@RequestBody Map<String, String> body) {
        var type = body.getOrDefault("type", "INFO");
        var msg  = body.getOrDefault("message", "");
        var payload = new NotificationPayload(UUID.randomUUID().toString(), type, msg, Instant.now());
        producer.sendNotification(payload);
        return ResponseEntity.accepted().body(Map.of(
                "status", "PUBLISHED",
                "id", payload.id(),
                "topic", "demo.notifications"
        ));
    }

}

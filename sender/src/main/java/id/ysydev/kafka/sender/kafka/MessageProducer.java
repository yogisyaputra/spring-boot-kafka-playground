package id.ysydev.kafka.sender.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    // ← ubah ke Object agar bisa kirim dua tipe
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MessageProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTo(String topic, String key, Object value) {
        kafkaTemplate.send(topic, key, value);
    }

    // generic: kirim ke topic tertentu + key + payload + traceId
    public void sendTo(String topic, String key, Object payload, String traceId) {
        Message<?> msg = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("x-trace-id", traceId)       // <-- header tracing
                .build();
        kafkaTemplate.send(msg);
    }

    // helper khusus MessagePayload (opsional)
    public void send(String topic, MessagePayload payload, String traceId) {
        sendTo(topic, payload.id(), payload, traceId);
    }


}

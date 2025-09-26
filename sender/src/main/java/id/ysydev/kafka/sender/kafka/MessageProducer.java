package id.ysydev.kafka.sender.kafka;

import id.ysydev.kafka.sender.model.MessagePayload;
import id.ysydev.kafka.sender.model.NotificationPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    // ← ubah ke Object agar bisa kirim dua tipe
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final String topic2;

    public MessageProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.topic}") String topic,
            @Value("${app.topic2}") String topic2) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.topic2 = topic2;
    }

    /** Kirim ke demo.messages */
    public void send(MessagePayload payload) {
        kafkaTemplate.send(topic, payload.id(), payload);
    }

    /** Kirim ke demo.notifications */
    public void sendNotification(NotificationPayload payload) {
        kafkaTemplate.send(topic2, payload.id(), payload);
    }
}

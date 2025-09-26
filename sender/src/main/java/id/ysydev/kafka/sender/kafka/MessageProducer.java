package id.ysydev.kafka.sender.kafka;

import org.springframework.kafka.core.KafkaTemplate;
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
}

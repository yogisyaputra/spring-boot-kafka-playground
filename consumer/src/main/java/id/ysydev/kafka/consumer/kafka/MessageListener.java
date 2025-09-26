package id.ysydev.kafka.consumer.kafka;

import id.ysydev.kafka.consumer.model.MessagePayload;
import id.ysydev.kafka.consumer.model.NotificationPayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener utama untuk konsumsi pesan dari Kafka.
 *
 * Perilaku:
 * - Mencetak ringkasan pesan yang diterima.
 * - Jika text mengandung "FAIL" (case-insensitive), lempar exception untuk mensimulasikan failure,
 *   sehingga mekanisme retry & DLT bisa teruji.
 *
 * Konfigurasi topik & groupId diambil dari application.yml.
 */
@Component
public class MessageListener {

    @Value("${app.topic}")
    private String topic;

    @KafkaListener(
            topics = "demo.messages",
            groupId = "message-consumer",
            containerFactory = "messageKafkaListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, MessagePayload> record) {
        var payload = record.value();

        // Simulasi failure untuk demo retry + DLT
        if (payload.text() != null && payload.text().toUpperCase().contains("FAIL")) {
            throw new RuntimeException("Simulated failure for testing DLT");
        }

        System.out.printf(
                "[CONSUMED] topic=%s partition=%d offset=%d key=%s id=%s text=%s at=%s%n",
                record.topic(), record.partition(), record.offset(), record.key(),
                payload.id(), payload.text(), payload.createdAt()
        );
    }


    @KafkaListener(topics = "demo.notifications",
            groupId = "notification-consumer",
            containerFactory = "notificationKafkaListenerContainerFactory")
    public void onNotification(ConsumerRecord<String, NotificationPayload> record) {
        var payload = record.value();

        // Simulasi failure untuk demo retry + DLT
        if (payload.message() != null && payload.message().toUpperCase().contains("FAIL")) {
            throw new RuntimeException("Simulated failure for testing DLT");
        }

        System.out.printf(
                "[CONSUMED] topic=%s partition=%d offset=%d key=%s id=%s text=%s at=%s%n",
                record.topic(), record.partition(), record.offset(), record.key(),
                payload.id(), payload.message(), payload.createdAt()
        );
    }
}

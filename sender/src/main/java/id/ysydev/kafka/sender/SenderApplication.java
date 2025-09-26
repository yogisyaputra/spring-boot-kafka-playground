package id.ysydev.kafka.sender;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Entry point aplikasi Sender.
 *
 * Tugas:
 * - Menyediakan REST API untuk menerima request dari client.
 * - Mengirim pesan ke Kafka (topic ditentukan lewat properti app.topic).
 *
 * Catatan:
 * - Bean NewTopic akan memastikan topic "demo.messages" tersedia saat aplikasi start
 *   (partisi=3, replika=1). Di cluster production, topik biasanya dibuat via DevOps, tapi
 *   untuk playground ini nyaman dibuat otomatis.
 */
@SpringBootApplication
public class SenderApplication {
    public static void main(String[] args) { SpringApplication.run(SenderApplication.class, args); }

    /**
     * Membuat topik utama jika belum ada.
     * Gunakan konfigurasi yang ringan untuk local dev.
     */
    @Bean
    NewTopic demoMessagesTopic() {
        return new NewTopic("demo.messages", 3, (short) 1);
    }

    @Bean
    NewTopic notificationsTopic() {
        return new NewTopic("demo.notifications", 3, (short) 1);
    }
}

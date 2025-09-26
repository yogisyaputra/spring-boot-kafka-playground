package id.ysydev.kafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point aplikasi Consumer.
 *
 * Tugas:
 * - Menjalankan listener Kafka untuk membaca pesan dari topic "demo.messages".
 * - (Opsional) Mengirim ke Dead Letter Topic jika terjadi error berulang.
 */
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) { SpringApplication.run(ConsumerApplication.class, args); }
}

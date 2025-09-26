package id.ysydev.kafka.sender.model;

import java.time.Instant;

/**
 * Payload pesan yang dikirim ke Kafka.
 *
 * Field:
 * - id: ID unik pesan; juga dipakai sebagai Kafka message key untuk konsistensi partisi.
 * - text: isi pesan.
 * - createdAt: timestamp pembuatan pesan di sisi sender.
 *
 * Menggunakan Java record agar ringkas dan immutable.
 */
public record MessagePayload(String id, String text, Instant createdAt) {}

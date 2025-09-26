package id.ysydev.kafka.consumer.model;

import java.time.Instant;

/**
 * Representasi payload yang diterima dari Kafka.
 * Harus konsisten struktur field-nya dengan yang dikirim oleh sender.
 */
public record MessagePayload(String id, String text, Instant createdAt) {}

package id.ysydev.kafka.consumer.model;

import java.time.Instant;

public record NotificationPayload(String id, String type, String message, Instant createdAt) {}

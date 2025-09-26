package id.ysydev.kafka.sender.model;

import java.time.Instant;

public record NotificationPayload(String id, String type, String message, Instant createdAt) {}

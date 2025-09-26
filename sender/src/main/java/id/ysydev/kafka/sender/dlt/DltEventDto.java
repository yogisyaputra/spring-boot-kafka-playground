package id.ysydev.kafka.sender.dlt;

public record DltEventDto(
        java.util.UUID id,
        String originalTopic,
        String key,
        String exception,
        String message,
        java.time.Instant receivedAt
) {}

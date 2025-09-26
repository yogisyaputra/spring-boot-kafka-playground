package id.ysydev.kafka.sender.dlt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dlt_events")
@Access(AccessType.FIELD)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DltEvent {
    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Column(name = "original_topic", nullable = false)
    private String originalTopic;

    @Column(name = "original_partition", nullable = false)
    private Integer originalPartition;

    @Column(name = "original_offset", nullable = false)
    private Long originalOffset;

    @Column(name = "msg_key")
    private String msgKey;

    @Column(name = "payload", columnDefinition = "text", nullable = false)
    private String payload;  // <-- TEXT

    @Column(name = "exception_class")
    private String exceptionClass;

    @Column(name = "exception_message", length = 2000)
    private String exceptionMessage;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "replayed_at")
    private Instant replayedAt;

    @Column(name = "replayed_by")
    private String replayedBy;
}

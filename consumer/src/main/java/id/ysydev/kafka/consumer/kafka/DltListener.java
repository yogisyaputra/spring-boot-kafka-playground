// consumer/kafka/DltListener.java
package id.ysydev.kafka.consumer.kafka;

import id.ysydev.kafka.consumer.entity.DltEvent;
import id.ysydev.kafka.consumer.repo.DltEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Component
public class DltListener {

    private final DltEventRepository repo;

    public DltListener(DltEventRepository repo) {
        this.repo = repo;
    }

    // Dengarkan dua DLT sekaligus
    @KafkaListener(
            topics = { "demo.messages.DLT", "demo.notifications.DLT" },
            groupId = "dlt-archiver",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void onDlt(
            ConsumerRecord<String, byte[]> rec,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_TOPIC, required = false) String originalTopic,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_PARTITION, required = false) Integer originalPartition,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_OFFSET, required = false) Long originalOffset,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_FQCN, required = false) String exClass,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) String exMessage
    ) {
        var id = UUID.randomUUID();
        DltEvent e = new DltEvent();
        e.setId(id);
        e.setOriginalTopic(originalTopic);
        e.setOriginalPartition(originalPartition);
        e.setOriginalOffset(originalOffset);
        e.setMsgKey(rec.key());
        e.setPayload(new String(rec.value(), StandardCharsets.UTF_8));
        e.setExceptionClass(exClass);
        e.setExceptionMessage(exMessage);
        e.setReceivedAt(Instant.now());
        e.setStatus("NEW");
        e.setReplayedAt(null);
        e.setReplayedBy(null);
        repo.save(e);
        System.out.printf("[DLT-ARCHIVE] id=%s orig=%s[%d@%d] key=%s reason=%s%n",
                id, e.getOriginalTopic(), e.getOriginalPartition(), e.getOriginalOffset(), e.getMsgKey(), e.getExceptionMessage());
    }
}

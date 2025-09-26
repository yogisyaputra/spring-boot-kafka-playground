// consumer/kafka/DltListener.java
package id.ysydev.kafka.consumer.listener;

import id.ysydev.kafka.consumer.dlt.DltEvent;
import id.ysydev.kafka.consumer.dlt.DltEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Component
public class ListenerDlt {

    private final DltEventRepository repo;

    public ListenerDlt(DltEventRepository repo) {
        this.repo = repo;
    }

    // Dengarkan dua DLT sekaligus
    @KafkaListener(
            topics = { "demo.messages.DLT", "demo.noretry.DLT" },
//            topicPattern = "demo\\..*\\.DLT",                 // <— kunci: tangkap semua DLT
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

        // Fallback kuat ketika header tidak ada (mis. re-DLT atau manual publish)
        String topicFallback = rec.topic().endsWith(".DLT") ? rec.topic().substring(0, rec.topic().length() - 4) : rec.topic();
        String origTopic = (originalTopic != null) ? originalTopic : topicFallback;
        int    origPart  = (originalPartition != null) ? originalPartition : rec.partition();
        long   origOff   = (originalOffset != null) ? originalOffset : rec.offset();
        var id = UUID.randomUUID();

        DltEvent e = new DltEvent();
        e.setId(id);
        e.setOriginalTopic(origTopic);
        e.setOriginalPartition(origPart);
        e.setOriginalOffset(origOff);
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

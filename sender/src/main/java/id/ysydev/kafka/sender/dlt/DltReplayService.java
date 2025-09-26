package id.ysydev.kafka.sender.dlt;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class DltReplayService {

    private final DltEventRepository repo;
    private final KafkaTemplate<String, byte[]> byteTemplate;

    public DltReplayService(DltEventRepository repo, KafkaTemplate<String, byte[]> byteTemplate) {
        this.repo = repo;
        this.byteTemplate = byteTemplate;
    }

    public DltEvent replay(UUID id, String replayedBy) {
        var e = repo.findById(id).orElseThrow();
        byte[] body = e.getPayload().getBytes(StandardCharsets.UTF_8);

        // kirim kembali ke topik asal; biarkan Kafka pilih partition by key
        var pr = new ProducerRecord<>(e.getOriginalTopic(), e.getMsgKey(), body);
        byteTemplate.send(pr);

        e.setStatus("REPLAYED");
        e.setReplayedAt(java.time.Instant.now());
        e.setReplayedBy(replayedBy);
        return repo.save(e);
    }
}

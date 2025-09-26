package id.ysydev.kafka.consumer.listener;

import id.ysydev.kafka.consumer.payload.MessagePayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerStagedRetry {

    private void maybeFail(String txt) {
        if (txt != null && txt.toUpperCase().contains("FAIL")) {
            throw new RuntimeException("Simulated failure");
        }
    }

    // 3) RETRY BERTAHAP -> @RetryableTopic (gunakan factory tanpa handler)
    @org.springframework.kafka.annotation.RetryableTopic
    @KafkaListener(
            topics = "demo.retry.staged",
            groupId = "staged-retry-consumer",
            containerFactory = "messageStagedRetryFactory"
    )
    public void stagedRetry(ConsumerRecord<String, MessagePayload> rec) {
        var p = rec.value(); maybeFail(p.text());
        System.out.printf("[STAGED-RETRY] key=%s id=%s text=%s topic=%s%n", rec.key(), p.id(), p.text(), rec.topic());
    }

}

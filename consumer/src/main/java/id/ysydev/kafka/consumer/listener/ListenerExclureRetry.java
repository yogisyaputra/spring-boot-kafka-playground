package id.ysydev.kafka.consumer.listener;

import id.ysydev.kafka.consumer.payload.MessagePayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
public class ListenerExclureRetry {
    private void maybeFail(String txt) {
        if (txt != null && txt.toUpperCase().contains("FAIL")) {
            throw new RuntimeException("Simulated failure");
        }
    }

    // 4) EXCLUDE EXCEPTION TERTENTU DARI RETRY (mis. IllegalArgumentException)
    @RetryableTopic(
            exclude = {IllegalArgumentException.class} // ini TIDAK akan di-retry -> langsung DLT
    )
    @KafkaListener(
            topics = "demo.retry.exclude",
            groupId = "exclude-retry-consumer",
            containerFactory = "messageExcludeFactory"
    )
    public void excludeRetry(ConsumerRecord<String, MessagePayload> rec) {
        var p = rec.value();
        if (p.text() != null && p.text().toUpperCase().contains("ILLEGAL")) {
            throw new IllegalArgumentException("Excluded from retry");
        }
        // Error lain tetap retry (staged) sesuai config global
        maybeFail(p.text());
        System.out.printf("[EXCLUDE-RETRY] key=%s id=%s text=%s topic=%s%n", rec.key(), p.id(), p.text(), rec.topic());
    }
}

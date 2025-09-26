package id.ysydev.kafka.consumer.listener;

import id.ysydev.kafka.consumer.payload.MessagePayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerSimpleRetry {
    private void maybeFail(String txt) {
        if (txt != null && txt.toUpperCase().contains("FAIL")) {
            throw new RuntimeException("Simulated failure");
        }
    }

    // 2) RETRY IN-PLACE (tidak bertahap) -> DefaultErrorHandler(simpleRetry)
    @KafkaListener(
            topics = "demo.retry.simple",
            groupId = "simple-retry-consumer",
            containerFactory = "messageSimpleRetryFactory"
    )
    public void simpleRetry(ConsumerRecord<String, MessagePayload> rec) {
        var p = rec.value(); maybeFail(p.text());
        System.out.printf("[SIMPLE-RETRY] key=%s id=%s text=%s%n", rec.key(), p.id(), p.text());
    }
}

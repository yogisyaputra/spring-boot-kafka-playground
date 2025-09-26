package id.ysydev.kafka.consumer.listener;

import id.ysydev.kafka.consumer.payload.MessagePayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ListenerNoRetry {

    private void maybeFail(String txt) {
        if (txt != null && txt.toUpperCase().contains("FAIL")) {
            throw new RuntimeException("Simulated failure");
        }
    }

    @KafkaListener(
            id = "noretry-listener",
            topics = "demo.noretry",
            groupId = "noretry-consumer",
            containerFactory = "messageNoRetryFactory"
    )
    public void noRetry(ConsumerRecord<String, MessagePayload> rec,
                        @Header(name="x-trace-id", required=false) String traceId) {
        var p = rec.value();
        maybeFail(p.text());
        System.out.printf("[NO-RETRY] key=%s id=%s trace=%s text=%s%n",
                rec.key(), p.id(), traceId, p.text());
    }
}

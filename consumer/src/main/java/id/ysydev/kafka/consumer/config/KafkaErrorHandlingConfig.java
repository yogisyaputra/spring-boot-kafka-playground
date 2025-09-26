package id.ysydev.kafka.consumer.config;

import id.ysydev.kafka.consumer.model.MessagePayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import java.util.function.BiFunction;

/**
 * Konfigurasi penanganan error pada Kafka listener.
 *
 * Komponen:
 * - DeadLetterPublishingRecoverer: mengarahkan pesan gagal ke DLT.
 * - DefaultErrorHandler: mengatur retry dengan exponential backoff.
 *
 * Alur:
 * 1) Jika listener melempar exception:
 *    - Di-retry sesuai kebijakan backoff (3x: 0.5s -> 1s -> 2s).
 * 2) Jika tetap gagal:
 *    - Dikirim ke topic DLT (demo.messages.DLT) dengan partisi yang sama.
 */
@Configuration
public class KafkaErrorHandlingConfig {

    @Value("${app.topic:demo.messages}")
    private String mainTopic;

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, MessagePayload> template) {
        // Tentukan target topic DLT dan partisinya (mirror partition dari record)
        BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> resolver = (rec, ex) ->
                new TopicPartition(rec.topic() + ".DLT", rec.partition());

        var recoverer = new DeadLetterPublishingRecoverer(template, resolver);

        // Kebijakan retry: 3 kali dengan exponential backoff
        var backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(500L);   // 0.5 detik
        backoff.setMultiplier(2.0);         // 0.5 -> 1 -> 2 detik
        backoff.setMaxInterval(5_000L);     // batas maksimum delay

        var handler = new DefaultErrorHandler(recoverer, backoff);

        // Contoh jika ingin ada exception tertentu yang TIDAK di-retry:
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }
}

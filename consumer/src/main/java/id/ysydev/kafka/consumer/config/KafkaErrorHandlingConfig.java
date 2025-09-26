package id.ysydev.kafka.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import java.util.function.BiFunction;

/**
 * Konfigurasi error handling untuk Kafka listener.
 * <p>
 * Kita sediakan 3 handler:
 * - noRetryErrorHandler : TIDAK ada retry (langsung kirim ke <topic>.DLT)
 * - simpleRetryErrorHandler : retry in-place (exponential backoff), lalu DLT
 * - errorHandler (umum) : retry in-place (bisa dipakai jika diperlukan)
 * <p>
 * Catatan:
 * - DLT akan diterbitkan ke "<topic>.DLT" dengan partisi yang sama seperti record asal.
 * - Handler ini DIPAKAI HANYA untuk container factory yang TIDAK menggunakan @RetryableTopic
 * (contoh: no-retry & simple-retry, dan DLT archiver). Untuk staged/exclude (RetryTopic),
 * JANGAN set CommonErrorHandler di factory; biarkan mekanisme RetryTopic yang menangani.
 */
@Configuration
public class KafkaErrorHandlingConfig {

    /**
     * Resolver tujuan DLT: arahan ke "<topic>.DLT" mirror partition.
     */
    private BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dltResolver() {
        return (rec, ex) -> new TopicPartition(rec.topic() + ".DLT", rec.partition());
    }

    /**
     * Handler umum (opsional) - retry 3x dengan exponential backoff, lalu DLT.
     * Jika tidak diperlukan, boleh diabaikan.
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, dltResolver());

        var backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(500L); // 0.5s
        backoff.setMultiplier(2.0);       // 0.5 -> 1 -> 2 detik
        backoff.setMaxInterval(5_000L);   // batas maksimum delay

        var handler = new DefaultErrorHandler(recoverer, backoff);

        // Contoh: exception tertentu langsung DLT (tanpa retry)
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }

    /**
     * NO-RETRY: langsung DLT (dipakai oleh messageNoRetryFactory & dltKafkaListenerContainerFactory).
     */
    @Bean
    public DefaultErrorHandler noRetryErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, dltResolver());

        // 0 retry (langsung arahkan ke DLT)
        var backoff = new ExponentialBackOffWithMaxRetries(0);

        var handler = new DefaultErrorHandler(recoverer, backoff);

        // Kamu juga bisa menandai exception tertentu sebagai tidak boleh di-retry (efeknya sama karena 0 retry)
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }

    /**
     * SIMPLE RETRY (in-place): retry 3x di topik yang sama, lalu DLT.
     * Dipakai oleh messageSimpleRetryFactory.
     */
    @Bean
    public DefaultErrorHandler simpleRetryErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, dltResolver());

        var backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(500L);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(5_000L);

        var handler = new DefaultErrorHandler(recoverer, backoff);

        // Contoh: jika ada exception yang ingin langsung DLT tanpa retry in-place:
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }
}

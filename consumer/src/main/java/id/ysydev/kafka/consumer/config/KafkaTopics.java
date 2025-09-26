package id.ysydev.kafka.consumer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Deklarasi topik Kafka untuk 4 mode + DLT masing-masing.
 * Catatan:
 * - Partitions=3 (silakan sesuaikan dengan concurrency dan throughput).
 * - Replicas=1 (dev). Di prod, set >=2/3 sesuai cluster.
 * - Retensi DLT diset 14 hari sebagai contoh.
 */
@Configuration
public class KafkaTopics {

    @Bean
    NewTopic t1_noRetry() {
        return TopicBuilder.name("demo.noretry").partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic t2_simpleRetry() {
        return TopicBuilder.name("demo.retry.simple").partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic t3_stagedRetry() {
        return TopicBuilder.name("demo.retry.staged").partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic t4_excludeRetry() {
        return TopicBuilder.name("demo.retry.exclude").partitions(1).replicas(1).build();
    }

    // ===== DLT UNTUK MASING-MASING TOPIK (retensi contoh: 14 hari) =====
    @Bean
    NewTopic dltNoRetry() {
        return TopicBuilder.name("demo.noretry.DLT")
                .partitions(1).replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(14L * 24 * 60 * 60 * 1000))
                .build();
    }

    @Bean
    NewTopic dltSimpleRetry() {
        return TopicBuilder.name("demo.retry.simple.DLT")
                .partitions(1).replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(14L * 24 * 60 * 60 * 1000))
                .build();
    }

    @Bean
    NewTopic dltStagedRetry() {
        return TopicBuilder.name("demo.retry.staged.DLT")
                .partitions(1).replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(14L * 24 * 60 * 60 * 1000))
                .build();
    }

    @Bean
    NewTopic dltExcludeRetry() {
        return TopicBuilder.name("demo.retry.exclude.DLT")
                .partitions(1).replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(14L * 24 * 60 * 60 * 1000))
                .build();
    }


    @Bean
    NewTopic messagesDLT() {
        return TopicBuilder.name("demo.messages.DLT")
                .partitions(1)
                .replicas(1)
                // contoh retensi 7 hari untuk DLT
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(7L * 24 * 60 * 60 * 1000))
                // biasanya DLT tidak compact
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }
}

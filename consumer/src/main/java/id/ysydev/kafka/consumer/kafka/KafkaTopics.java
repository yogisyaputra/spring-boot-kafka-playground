package id.ysydev.kafka.consumer.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Deklarasi topik-topik yang diperlukan oleh consumer.
 *
 * Untuk keperluan demo/local dev, kita buat DLT otomatis.
 * Di produksi, sebaiknya topik dikelola terpisah (Terraform/CLI/ops).
 */
@Configuration
class KafkaTopics {

    /**
     * Dead Letter Topic untuk pesan yang gagal diproses setelah retry.
     * Nama DLT = "<mainTopic>.DLT" => "demo.messages.DLT"
     */
    @Bean
    NewTopic messagesDLT() {
        return TopicBuilder.name("demo.messages.DLT")
                .partitions(3)
                .replicas(1)
                // contoh retensi 7 hari untuk DLT
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(7L * 24 * 60 * 60 * 1000))
                // biasanya DLT tidak compact
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }

    @Bean
    NewTopic notificationsDLT() {
        return TopicBuilder.name("demo.notifications.DLT")
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(7L * 24 * 60 * 60 * 1000))
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }
}

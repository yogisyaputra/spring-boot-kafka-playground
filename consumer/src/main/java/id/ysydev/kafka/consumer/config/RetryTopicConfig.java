package id.ysydev.kafka.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;

@Configuration
public class RetryTopicConfig {
    @Bean
    public RetryTopicConfiguration retryCfg(KafkaTemplate<?,?> template) {
        return RetryTopicConfigurationBuilder
                .newInstance()
                .fixedBackOff(5_000)
                .maxAttempts(4)
                .retryTopicSuffix("-retry")
                .dltSuffix(".DLT")
                .autoCreateTopics(true, 3, (short) 1)
                // ⬇️ ini kuncinya
                .excludeTopic("demo.noretry")
                .excludeTopic("demo.retry.simple")
                // (opsional) boleh sekalian include yang diinginkan:
                .includeTopic("demo.retry.staged")
                .includeTopic("demo.retry.exclude")
                .create(template);
    }
}

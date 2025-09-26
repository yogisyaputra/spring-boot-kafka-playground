package id.ysydev.kafka.consumer.config;

import id.ysydev.kafka.consumer.model.MessagePayload;
import id.ysydev.kafka.consumer.model.NotificationPayload;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerFactories {

    @Bean
    public ConsumerFactory<String, MessagePayload> messageConsumerFactory(
            KafkaProperties props,
            DefaultKafkaConsumerFactoryCustomizer customizer) {

        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties(null));
        var value = new JsonDeserializer<>(MessagePayload.class);
        value.addTrustedPackages("id.ysydev.kafka.*");
        var factory = new DefaultKafkaConsumerFactory<>(cfg, new StringDeserializer(), value);
        customizer.customize(factory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, NotificationPayload> notificationConsumerFactory(
            KafkaProperties props,
            DefaultKafkaConsumerFactoryCustomizer customizer) {

        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties(null));
        var value = new JsonDeserializer<>(NotificationPayload.class);
        value.addTrustedPackages("id.ysydev.kafka.*");
        var factory = new DefaultKafkaConsumerFactory<>(cfg, new StringDeserializer(), value);
        customizer.customize(factory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagePayload>
    messageKafkaListenerContainerFactory(
            ConsumerFactory<String, MessagePayload> cf,
            DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, MessagePayload>();
        f.setConsumerFactory(cf);
        f.setCommonErrorHandler(errorHandler); // ⟵ penting!
        return f;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationPayload>
    notificationKafkaListenerContainerFactory(
            ConsumerFactory<String, NotificationPayload> cf,
            DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, NotificationPayload>();
        f.setConsumerFactory(cf);
        f.setCommonErrorHandler(errorHandler); // ⟵ penting!
        return f;
    }


    // --- DLT (value as byte[]) ---
    @Bean
    public ConsumerFactory<String, byte[]> dltConsumerFactory(
            KafkaProperties props,
            DefaultKafkaConsumerFactoryCustomizer customizer
    ) {
        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties(null));
        cfg.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        cfg.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.ByteArrayDeserializer.class);
        var factory = new DefaultKafkaConsumerFactory<String, byte[]>(cfg);
        customizer.customize(factory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> dltKafkaListenerContainerFactory(
            ConsumerFactory<String, byte[]> cf,
            DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        f.setConsumerFactory(cf);
        // biasanya DLT listener tidak di-retry lagi; tapi tidak masalah jika handler terpasang.
        f.setCommonErrorHandler(errorHandler);
        return f;
    }


}

package id.ysydev.kafka.consumer.config;

import id.ysydev.kafka.consumer.payload.MessagePayload;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kumpulan ConsumerFactory & ConcurrentKafkaListenerContainerFactory.
 * <p>
 * Catatan penting:
 * - Kita membungkus JsonDeserializer dengan ErrorHandlingDeserializer supaya error deserialization
 * bisa diteruskan ke error handler / retry-topic / DLT.
 * - Untuk listener yang memakai @RetryableTopic (staged/exclude), JANGAN set CommonErrorHandler
 * di factory-nya — mekanisme retry & DLT diatur oleh RetryTopicConfiguration.
 * - Untuk mode no-retry & simple-retry (in-place), kita pasang CommonErrorHandler via @Qualifier.
 */
@Configuration
public class KafkaConsumerFactories {

    private static final String TRUSTED_PACKAGES = "id.ysydev.kafka.*";

    // =====================================================================================
    // =                          CONSUMER FACTORY (Value: MessagePayload)                 =
    // =====================================================================================

    @Bean
    public ConsumerFactory<String, MessagePayload> messageConsumerFactory(
            KafkaProperties props,
            DefaultKafkaConsumerFactoryCustomizer customizer
    ) {
        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties(null));

        // KEY deserializer
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // VALUE deserializer dibungkus ErrorHandlingDeserializer -> JsonDeserializer
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        cfg.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Lock default type (karena producer tidak kirim type headers)
        cfg.put(JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES);
        cfg.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MessagePayload.class.getName());
        cfg.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        var factory = new DefaultKafkaConsumerFactory<String, MessagePayload>(cfg);
        customizer.customize(factory);
        return factory;
    }


    // =====================================================================================
    // =                           CONSUMER FACTORY (Value: byte[]) for DLT                =
    // =====================================================================================

    @Bean
    public ConsumerFactory<String, byte[]> dltConsumerFactory(
            KafkaProperties props,
            DefaultKafkaConsumerFactoryCustomizer customizer
    ) {
        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties(null));

        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

        var factory = new DefaultKafkaConsumerFactory<String, byte[]>(cfg);
        customizer.customize(factory);
        return factory;
    }

    // =====================================================================================
    // =             CONTAINER FACTORIES: 4 MODE + DLT (MessagePayload unless noted)       =
    // =====================================================================================

    /**
     * Mode 1: NO-RETRY (langsung DLT). Gunakan noRetryErrorHandler.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagePayload> messageNoRetryFactory(
            ConsumerFactory<String, MessagePayload> cf,
            @Qualifier("noRetryErrorHandler") DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, MessagePayload>();
        f.setConsumerFactory(cf);
        f.setCommonErrorHandler(errorHandler); // langsung DLT
        return f;
    }

    /**
     * Mode 2: SIMPLE RETRY (in-place): retry 3x dengan backoff lalu DLT.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagePayload> messageSimpleRetryFactory(
            ConsumerFactory<String, MessagePayload> cf,
            @Qualifier("simpleRetryErrorHandler") DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, MessagePayload>();
        f.setConsumerFactory(cf);
        f.setCommonErrorHandler(errorHandler); // in-place retry
        return f;
    }

    /**
     * Mode 3: STAGED RETRY (retry topics) - dipakai oleh listener beranotasi @RetryableTopic.
     * JANGAN pasang CommonErrorHandler di sini.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagePayload> messageStagedRetryFactory(
            ConsumerFactory<String, MessagePayload> cf
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, MessagePayload>();
        f.setConsumerFactory(cf);
        return f;
    }

    /**
     * Mode 4: EXCLUDE jenis error tertentu (via @RetryableTopic(exclude=...)).
     * JANGAN pasang CommonErrorHandler di sini juga.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagePayload> messageExcludeFactory(
            ConsumerFactory<String, MessagePayload> cf
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, MessagePayload>();
        f.setConsumerFactory(cf);
        return f;
    }

    /**
     * Factory untuk DLT archiver (value=byte[]). Disarankan pakai noRetryErrorHandler
     * agar tidak terjadi loop pada error saat mengarsipkan.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> dltKafkaListenerContainerFactory(
            ConsumerFactory<String, byte[]> cf,
            @Qualifier("noRetryErrorHandler") DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        f.setConsumerFactory(cf);
        f.setCommonErrorHandler(errorHandler);
        return f;
    }
}

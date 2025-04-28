package ru.yandex.hw5_task1_consumer;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Consumer {


    public static void main(String[] args) {

        String HOST = "rc1a-lflcmbh2adbn4q0q.mdb.yandexcloud.net:9091, rc1b-ad4hr73e491kub9k.mdb.yandexcloud.net:9091, rc1d-vk669mu91k78rb96.mdb.yandexcloud.net:9091";
        String TOPIC = "users";
        String USER = "consumer";
        String PASS = "password";
        String TS_FILE = "/home/andrey/005_kafka/ssl.jks";
        String TS_PASS = "password";

        String schemaRegistryUrl = "rc1a-lflcmbh2adbn4q0q.mdb.yandexcloud.net:443";

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, USER, PASS);
        String GROUP = "test";

        String deserializer = StringDeserializer.class.getName();
        Properties props = new Properties();
        props.put("bootstrap.servers", HOST);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("group.id", GROUP);
        props.put("key.deserializer", deserializer);
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config", jaasCfg);
        props.put("ssl.truststore.location", TS_FILE);
        props.put("ssl.truststore.password", TS_PASS);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonSchemaDeserializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);

        KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Caught shutdown signal, closing consumer...");
            consumer.wakeup();
            latch.countDown();
        }));

        try {
            while (true) {
                ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, User> record : records) {
                    System.out.printf("Message on %s:%n%s%n", record.topic(), record.value());
                    if (record.headers() != null) {
                        System.out.printf("Headers: %s%n", record.headers());
                    }
                }
            }
        } catch (WakeupException e) {
            // Expected during shutdown
        } finally {
            consumer.close();
            System.out.println("Consumer closed");
            latch.countDown();
        }
    }
}

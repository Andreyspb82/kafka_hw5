package ru.yandex.hw5_task2_consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerString {

    public static void main(String[] args) {

        String HOST = "rc1a-lflcmbh2adbn4q0q.mdb.yandexcloud.net:9091, rc1b-ad4hr73e491kub9k.mdb.yandexcloud.net:9091, rc1d-vk669mu91k78rb96.mdb.yandexcloud.net:9091";
        String TOPIC = "nifi-topic";
        String USER = "consumer";
        String PASS = "password";
        String TS_FILE = "/home/andrey/005_kafka/005_result/task_2/infra/security/sslnifi.jks";
        String TS_PASS = "password";

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, USER, PASS);
        String GROUP = "test";

        String deserializer = StringDeserializer.class.getName();
        Properties props = new Properties();
        props.put("bootstrap.servers", HOST);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("group.id", GROUP);
        props.put("key.deserializer", deserializer);
        props.put("value.deserializer", deserializer);
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config", jaasCfg);
        props.put("ssl.truststore.location", TS_FILE);
        props.put("ssl.truststore.password", TS_PASS);

        // Создание Kafka consumer
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));


                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Получено сообщение: value=%s, partition=%d, offset=%d%n",
                            record.value(), record.partition(), record.offset());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

}

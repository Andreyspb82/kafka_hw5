package ru.yandex.hw5_task1_produser;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Producer {

    public static void main(String[] args) {
        String HOST = "rc1a-lflcmbh2adbn4q0q.mdb.yandexcloud.net:9091, rc1b-ad4hr73e491kub9k.mdb.yandexcloud.net:9091, rc1d-vk669mu91k78rb96.mdb.yandexcloud.net:9091";
        String TOPIC = "users";
        String USER = "producer";
        String PASS = "password";
        String TS_FILE = "/home/andrey/005_kafka/ssl";
        String TS_PASS = "password";
        String schemaRegistryUrl = "https://rc1a-lflcmbh2adbn4q0q.mdb.yandexcloud.net:443";
        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, USER, PASS);
        System.out.println("jaasCfg = " + jaasCfg);

        String serializer = StringSerializer.class.getName();
        Properties props = new Properties();
        props.put("bootstrap.servers", HOST);
        props.put("acks", "all");
        props.put("session.timeout.ms", "45000");
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config", jaasCfg);
        props.put("ssl.truststore.location", TS_FILE);
        props.put("ssl.truststore.password", TS_PASS);
        props.put("key.serializer", serializer);
        props.put("value.serializer", KafkaJsonSchemaSerializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("auto.register.schemas", true);
        props.put("use.latest.version", true);
        props.put("basic.auth.credentials.source", "USER_INFO");
        props.put("basic.auth.user.info", "producer:password");


        SchemaRegistryClient schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryUrl, 10);
        org.apache.kafka.clients.producer.Producer<String, User> producer = new KafkaProducer<>(props);

        // Register schema
        String userSchema = "{\"$schema\": \"http://json-schema.org/draft-07/schema#\", \"title\": \"User\", \"type\": \"object\", \"properties\": { \"name\": { \"type\": \"string\" }, \"favoriteNumber\": { \"type\": \"integer\" }, \"favoriteColor\": { \"type\": \"string\" } }, \"required\": [\"name\", \"favoriteNumber\", \"favoriteColor\"] }";
        SchemaRegistryHelper.registerSchema(schemaRegistryClient, TOPIC, userSchema);

        User user = new User("First user", 42, "blue");

        ProducerRecord<String, User> record = new ProducerRecord<>(TOPIC, user.getName(), user);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Delivery failed: " + exception.getMessage());
                exception.printStackTrace(System.err);
            } else {
                System.out.printf("Delivered message to topic %s [%d] at offset %d%n",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
        producer.close();
    }
}

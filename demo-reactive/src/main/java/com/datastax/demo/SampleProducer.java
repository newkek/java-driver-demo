package com.datastax.demo;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.javatuples.Pair;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Simple producer of new values adding to the Kafka stream.
 */
public class SampleProducer {

    static final String BOOTSTRAP_SERVERS = "localhost:9092";
    static final String TOPIC = "demo-topic";
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");

    private final SenderOptions<Integer, Integer> senderOptions;
    private final KafkaSender<Integer, Integer> sender;


    public static void main(String[] args) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        SampleProducer producer = new SampleProducer(BOOTSTRAP_SERVERS);

        producer.sendMessages(TOPIC, latch, 1, 1); // andy plays with the roundup gang
//        producer.sendMessages(TOPIC, latch, 1, 2); // andy plays with buzz
//        producer.sendMessages(TOPIC, latch, 1, 3); // andy plays with slinky

//        producer.sendMessages(TOPIC, latch, 2, 1); // bonnie plays with the roundup gang
//        producer.sendMessages(TOPIC, latch, 2, 2); // bonnie plays with buzz
//        producer.sendMessages(TOPIC, latch, 2, 3); // bonnie plays with slinky

        latch.await(10, TimeUnit.SECONDS);
        producer.close();
    }

    public void sendMessages(String topic, CountDownLatch latch, Integer userId, Integer productUsed) {
        sender.send(
                Flux.just(Pair.with(userId, productUsed))
                    .map(data -> SenderRecord.create(new ProducerRecord<>(topic, data.getValue0(), data.getValue1()), data.getValue0())))
                    .subscribe(messageSentSuccessHandler(latch), error -> System.out.println("error happened = " + error.getMessage()));
    }

    private static Consumer<SenderResult<Integer>> messageSentSuccessHandler(CountDownLatch latch){
        return result -> {
            RecordMetadata metadata = result.recordMetadata();
            System.out.printf("Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                    result.correlationMetadata(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    DATE_FORMAT.format(new Date(metadata.timestamp())));
            latch.countDown();
        };
    }

    private SenderOptions<Integer, Integer> ininKafkaOptions(String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        return SenderOptions.create(props);
    }

    public SampleProducer(String bootstrapServers) {
        senderOptions = ininKafkaOptions(bootstrapServers);
        sender = KafkaSender.create(senderOptions);
    }

    public void close() {
        sender.close();
    }
}

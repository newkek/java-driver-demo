package com.datastax.demo;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.javatuples.Pair;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.datastax.demo.SampleProducer.*;

public class ReactiveMain {

    private final ReceiverOptions<Integer, Integer> receiverOptions;

    private final DseSession session;


    public static void main(String[] args) throws Exception {
        int count = 20;
        CountDownLatch latch = new CountDownLatch(count);

        try (DseSession session = DseSession.builder().withKeyspace("meetup_demo").build()) {


            ReactiveMain consumer = new ReactiveMain(BOOTSTRAP_SERVERS, session);
            Disposable disposable = consumer.setupConsumerPipeline(TOPIC, latch);


            latch.await(20, TimeUnit.MINUTES);
            disposable.dispose();
        }
    }

    public Disposable setupConsumerPipeline(String topic, CountDownLatch latch) {
        ReceiverOptions<Integer, Integer> options = receiverOptions.subscription(Collections.singleton(topic));
        Flux<ReceiverRecord<Integer, Integer>> kafkaFlux = KafkaReceiver.create(options).receive();

        return kafkaFlux

                .doOnNext(messageReceivedHandler)

                // go fetch products names from product group id in Product table
                .flatMap(record -> Flux.from(
                        session.executeReactive(
                                SimpleStatement.newInstance("SELECT name FROM product WHERE id = ?", record.value())))
                        .map(row -> Pair.with(record, row))
                )

                // insert into Usage table a new entry of a usage
                .flatMap(pair -> session.executeReactive(
                        SimpleStatement.newInstance("INSERT INTO usage(time, userId, productName) VALUES (?, ?, ?)",
                                pair.getValue0().key(),
                                pair.getValue1().getString("name"),
                                Instant.ofEpochMilli(pair.getValue0().timestamp())))
                )

                .subscribe(success -> latch.countDown(), error -> latch.countDown());
    }

    public ReactiveMain(String bootstrapServers, DseSession session) {
        this.session = session;
        this.receiverOptions = initKafkaOptions(bootstrapServers);
    }

    private static final Consumer<ReceiverRecord<Integer, Integer>> messageReceivedHandler = record -> {
        ReceiverOffset offset = record.receiverOffset();
        System.out.printf("Received message: topic-partition=%s offset=%d timestamp=%s key=%d value=%s\n",
                offset.topicPartition(),
                offset.offset(),
                DATE_FORMAT.format(new Date(record.timestamp())),
                record.key(),
                record.value());
        offset.acknowledge();
    };

    private ReceiverOptions<Integer, Integer> initKafkaOptions(String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return ReceiverOptions.create(props);
    }

}

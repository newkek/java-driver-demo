# java-driver-demo

This is a simple example set of mini java programs that make use of DataStax drivers new capabilities.

It is separated in the following sub projects:

- _demo-querybuilder_: Small set of examples using the driver's new Query Builder
- _demo-mapper_: Example of the new Object Relational Mapper
- _demo-reactive_: Simple example of using the new Reactive Streams API exposed by the new driver and integrate it into an existing `Publisher` workflow

## Run it

Important: the java driver's new object mapper has not been released yet and is still in development. So to be able to use the mapper demo requires building and installing locally the DataStax Apache Cassandra driver from the branch `java2078`:

```
> git clone https://github.com/datastax/java-driver.git
> git checkout java2078
> mvn clean install -DskipTests
```

All the examples require that a local Apache Cassandra or a local DataStax Enterprise node be running.

The demo's modules have main methods and are supposed to be run in a certain order for everything to work:

1. Run the main from _demo-querybuilder_ AND _demo-mapper_ (`simple` package) in irrelevant order.
2. Run the main from _demo-reactive_

#### Note

_demo-reactive_ will require that a local Kafka cluster be running, in which a topic "demo-topic" needs to be created too. 

On my side I created the kafka topic with 

```
$KAFKA_DIR/bin/kafka-topics.sh --zookeeper localhost:2181 --create --replication-factor 1 --partitions 2 --topic demo-topic)
```

## Configuring

All the submodules have some pre-created `application.conf` configuration files created, which should be directly usable to configure the driver.

There are already some configurations setup in there that can be uncommented to allow having the driver logging queries being executed under the hood during the demo programs.
package com.hpwv.locationprocessor.processor;

import com.hpwv.locationprocessor.data.LocationUpdate;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class LocationUpdateProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Value(value = "${location-processor.input-topic}")
    public String inputTopic;

    @Value(value = "${location-processor.output-topic-postfix}")
    public String outputTopicPostfix;

    @Value(value = "${PROCESSOR_TYPE}")
    public String processorType;

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        final JsonSerde<LocationUpdate> locationUpdateSerde = new JsonSerde<>(LocationUpdate.class);

        KStream<String, LocationUpdate> messageStream = streamsBuilder
                .stream(inputTopic, Consumed.with(STRING_SERDE, locationUpdateSerde));

        messageStream
                .filter((k, v) -> processorType.equals(v.type))
                .to(getOutputTopicName());

        /*KTable<String, LocationUpdate> wordCounts = messageStream
                .toTable().mapValues((key, locationUpdate) -> locationUpdate);

        wordCounts.toStream().to(getOutputTopicName());*/
    }

    private String getOutputTopicName() {
        // return processorType + outputTopicPostfix;
        return "output-topic";
    }
}

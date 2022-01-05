package com.hpwv.locationprocessor.processor;

import com.hpwv.locationprocessor.data.LocationUpdate;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class LocationUpdateProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Value(value = "${spring.kafka.input-topic}")
    public String inputTopic;

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        final JsonSerde<LocationUpdate> locationUpdateSerde = new JsonSerde<>(LocationUpdate.class);

        KStream<String, LocationUpdate> messageStream = streamsBuilder
                .stream(inputTopic, Consumed.with(STRING_SERDE, locationUpdateSerde));

        KTable<String, LocationUpdate> wordCounts = messageStream
                .toTable().mapValues((key, locationUpdate) -> locationUpdate);

        wordCounts.toStream().to("output-topic");
    }
}

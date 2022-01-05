package com.hpwv.locationprocessor;

import com.hpwv.locationprocessor.data.LocationUpdate;
import com.hpwv.locationprocessor.processor.LocationUpdateProcessor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

public class LocationUpdateProcessorTests {

    private LocationUpdateProcessor locationUpdateProcessor;

    @BeforeEach
    void setUp() {
        locationUpdateProcessor = new LocationUpdateProcessor();
        locationUpdateProcessor.inputTopic = "location-update";
    }

    @Test
    void givenInputMessages_whenProcessed_thenCorrectOutputIsProduced() {
        locationUpdateProcessor.processorType = "car";

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        locationUpdateProcessor.buildPipeline(streamsBuilder);
        Topology topology = streamsBuilder.build();

        Properties props = new Properties();
        props.setProperty(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.setProperty(DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());

        Map<String, String> deserializerConfig = new HashMap<>();
        deserializerConfig.put(JsonDeserializer.TRUSTED_PACKAGES, "com.hpwv.*");

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, props)) {

            TestInputTopic<String, LocationUpdate> inputTopic = topologyTestDriver
                    .createInputTopic("location-update", new StringSerializer(), new JsonSerializer<>());

            JsonDeserializer<LocationUpdate> deserializer = new JsonDeserializer<>();
            deserializer.configure(deserializerConfig, false);

            TestOutputTopic<String, LocationUpdate> outputTopic = topologyTestDriver
                    .createOutputTopic("output-topic", new StringDeserializer(), deserializer);

            LocationUpdate carUpdate = new LocationUpdate();
            carUpdate.type = "car";
            LocationUpdate otherUpdate = new LocationUpdate();
            otherUpdate.type = "bike";

            inputTopic.pipeInput("1", carUpdate);
            inputTopic.pipeInput("2", carUpdate);
            inputTopic.pipeInput("3", otherUpdate);

            assertThat(outputTopic.readKeyValuesToList().size()).isEqualTo(2);
        }
    }

}

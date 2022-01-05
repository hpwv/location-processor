package com.hpwv.locationprocessor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LocationProcessorApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LocationProcessorApplication.class)
                .run(args);
    }

}

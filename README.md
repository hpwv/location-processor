# Location Processor
## Introduction
There are different types of location processors: car, bike, pedestrian.

The `docker-compose.yml` defines which processors will be started.
Docker compose is configured to re-start the location processors unless you explicitly 
shut them down. The reason for this is that they will fail to start until data is being produced
that can be processed, as the input topic will not exist until then.

## Prerequisites
Install `docker` and `docker-compose`.
Run `docker compose build` to build the docker containers ahead of running them.

Make sure the Kafka Cluster is up and running before starting the producers.

## Running
Simply run `docker compose up` if you want to see the log output in the current
terminal session or `docker compose up -d` if you want to start it in the background.


#!/bin/bash

# Starte den Jobmanager
/opt/flink/bin/jobmanager.sh start

# Warte ein wenig, damit der Jobmanager vollständig hochgefahren ist (optional)
sleep 10

# Starte den Flink-Job
/opt/flink/bin/flink run /opt/flink/jobs/recommenderSystem-1.0-SNAPSHOT.jar

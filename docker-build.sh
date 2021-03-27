#!/usr/bin/env bash

# проверяем, установлен ли maven, если да, то собираем проект
if command -v mvn &> /dev/null
then
  mvn -DskipTests package
fi

docker build -t dreamworkerln/streammonitoring-tbot:latest -f zinfrastructure/docker/tbot/Dockerfile .
docker build -t dreamworkerln/streammonitoring-monitor:latest -f zinfrastructure/docker/monitor/Dockerfile .
docker build -t dreamworkerln/streammonitoring-relay:latest -f zinfrastructure/docker/relay/Dockerfile .


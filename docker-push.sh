#!/usr/bin/env bash

#rm ztmp/*
#mkdir -p ztmp
#docker save dreamworkerln/streammonitoring-tbot:latest > ztmp/streammonitoring-tbot.tar
#docker save dreamworkerln/streammonitoring-monitor:latest > ztmp/streammonitoring-monitor.tar
#docker save dreamworkerln/streammonitoring-relay:latest > ztmp/streammonitoring-relay.tar


docker push dreamworkerln/streammonitoring-monitor
docker push dreamworkerln/streammonitoring-tbot
docker push dreamworkerln/streammonitoring-relay

echo "PULLING In REMOTE ==============================================================="

ssh p1-root bash -c "'
docker pull dreamworkerln/streammonitoring-monitor
docker pull dreamworkerln/streammonitoring-tbot
'"

ssh u3-root bash -c "'
docker pull dreamworkerln/streammonitoring-relay
'"


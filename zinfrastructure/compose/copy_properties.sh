#!/bin/bash

mkdir -p monitor
mkdir -p tbot

cp ../../monitor/src/main/resources/application.properties monitor/application.properties
cp ../../monitor/src/main/resources/monitor.properties monitor/monitor.properties

cp ../../tbot/src/main/resources/application.properties tbot/application.properties
cp ../../tbot/src/main/resources/tbot.properties tbot/tbot.properties

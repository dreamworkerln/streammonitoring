# streammonitoring
#### Flussonic media server streams monitoring and notification via telegram bot

## DEV NFO

#### bash-ncat webserver
```
ncat -lkv localhost 7070 -c 'tee /dev/stdout'
```

#### rabbitmq watch messages
```
watch -n1 sudo rabbitmqadmin list queues name messages messages_unacknowledged
sudo rabbitmqadmin delete queue name=test.rpc
```

#### rabbitmq watch messages web admin gui
```
http://localhost:15672/
```

#### mvn show transitive dependencies
```
mvn dependency:tree
```
#### hazelcast remove warning java-modules 9+
##### add to RUN JVM OPTIONS
```
--add-modules
java.se
--add-exports
java.base/jdk.internal.ref=ALL-UNNAMED
--add-opens
java.base/java.lang=ALL-UNNAMED
--add-opens
java.base/java.nio=ALL-UNNAMED
--add-opens
java.base/sun.nio.ch=ALL-UNNAMED
--add-opens
java.management/sun.management=ALL-UNNAMED
--add-opens
jdk.management/com.sun.management.internal=ALL-UNNAMED
```



#### Running from IDE:
```
Set Working directory in Run Configurations for each project to:

monitor - streammonitoring/monitor
tbot - streammonitoring/tbot
relay - streammonitoring/relay

or will got loaded wrong .properties files
``` 

#### check that sensitive data not in repository
```
git log --pretty=format: --name-only --diff-filter=A  | sort -u | grep "\.properties"
git log --pretty=format: --name-only --diff-filter=A  | sort -u | grep "docker"
```


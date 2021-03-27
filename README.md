# streammonitoring
Flussonic media server streams monitoring and notification via telegram bot

Running from IDE:
Set Working directory in Run Configurations to:
streammonitoring/monitor to monitor
streammonitoring/tbot to tbot
Or will load wrong properties file


##NFO

###bash-ncat webserver
ncat -lkv localhost 7070 -c 'tee /dev/stdout'

###rabbitmq watch messages
watch -n1 sudo rabbitmqadmin list queues name messages messages_unacknowledged

###rabbitmq watch messages admin gui:
http://localhost:15672/ 


# streammonitoring
#### Flussonic media server / watcher streams monitoring and notification via telegram bot

#### Installation

##### Configure
``` 
cd zinfrastructure/compose/
copy_properties.sh
```
will create folders monitor and tbot and copy there *.properties

##### configure monitor

edit monitor/monitor.properties
```
# mediaserver
media.server.list=video1.streamer.ru,video2.streamer.ru,video3.streamer.ru
media.server.username=USERNAME
media.server.password=PASSWORD

# watcher
watcher.address=watcher.streamer.ru
watcher.username=USERNAME
watcher.password=PASSWORD
watcher.token=TOKEN
watcher.use=true

# misc
# streams names should be unique on all media servers
check.stream.global.uniqueness=true
refresh.interval.sec=60
```

Set
```   
media.server.list
media.server.username
media.server.password
```
(assuming username/password are the same on all mediaservers)
    
If you want to monitor cameras streams from Flussonic Watcher  
set watcher address, username, password, and token  
  
howto obtain token:   
https://flussonic.github.io/watcher-docs/api-examples.html#login
```
curl -sS -f \
    -H 'content-type:application/json' \
    -d '{"login": "${WATCHER_USERNAME}", "password": "${WATCHER_PASSWORD}"}' \
    ${WATCHER_URL}/vsaas/api/v2/auth/login \
    | jq -r '.session'
```    
set
``` 
watcher.use=true
```
and
```
check.stream.global.uniqueness=true
```
If you don't want to use Watcher set
```
check.stream.global.uniqueness=false
```
and delete all 
```
watcher.*
```
values  
<br>
##### configure tbot

edit tbot/tbot.properties  
Set
```   
telegram.bot.token=MY_TELEGRAM_BOT_TOKEN
telegram.bot.group=MY_TELEGRAM_BOT_GROUP
```
telegram.bot.group currently not used (where bot may publish some notifications)
<br>
<br>   
You may compile for yourself and use you own docker images names, edit
```   
docker-build.sh
```   
and use you oun image names, then edit
```   
zinfrastructure/compose/docker-compose.yml
```   
and update images names
<br>
<br>     
#### Run
```
cd zinfrastructure/compose/
start.sh
```
     

#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids.ec
port=9080
mosesserver=/disk3/bhaddow/moses-server/server/mosesserver
logdir=/disk4/translation-server/logs.ec

# fr-en it-en
#configs="/disk4/webtrans-models/acquis/moses.weight-reused.ini.9 /disk4/webtrans-models/acquis/moses.weight-reused.ini.11"
configs="/disk4/webtrans-models/acquis/moses.weight-reused.ini.9"

start() {
    echo "Starting moses servers"
    if [ -f $pidfile ]; then
        echo >&2 "Error: moses already running"
        exit 1
    fi
    for config in $configs; do
        nohup $mosesserver -f $config --server-port $port >& $logdir/mosesserver.$port.log &
        echo "$!" >> $pidfile
        port=`expr $port + 1`
    done

}

stop() {
    echo "Stopping moses servers"
    if [ ! -f $pidfile ]; then
        echo >&2 "Error: moses not running"
        exit 1
    fi
    for pid in `cat $pidfile`; do
        kill $pid
    done
    rm $pidfile
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    *)
    echo $"Usage: $0 {start|stop}"
    exit 1
esac

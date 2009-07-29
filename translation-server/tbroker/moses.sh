#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids
port=8080
configs="/disk4/translation-server/models/fr-en-news-commentary/moses.ini"
mosesserver=/disk3/bhaddow/moses-server/server/mosesserver
logdir=/disk4/translation-server/logs


start() {
    echo "Starting moses servers"
    if [ -f $pidfile ]; then
        echo >&2 "Error: moses already running"
        exit 1
    fi
    for config in $configs; do
        log=$logdir/mosesserver.$port
            nohup $mosesserver -f $config --server-port $port >>$log.out 2>> $log.err  &
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

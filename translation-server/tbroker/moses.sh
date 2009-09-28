#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids
port=8080
model_dir=/disk4/translation-server/models
configs="$model_dir/europarl/de-en/moses.ini.2 $model_dir/europarl/en-de/moses.ini.2 $model_dir/europarl/es-en/moses.ini.4 $model_dir/europarl/fr-en/moses.ini.2"
mosesserver=/disk3/bhaddow/moses-server/server/mosesserver
mosesargs="-search-algorithm 1 -cube-pruning-pop-limit 500 -s 500"
logdir=/disk4/translation-server/logs

ulimit -c unlimited

start() {
    echo "Starting moses servers"
    if [ -f $pidfile ]; then
        echo >&2 "Error: moses already running"
        exit 1
    fi
    for config in $configs; do
        log=$logdir/mosesserver.$port
            nohup $mosesserver $mosesargs -f $config --server-port $port >>$log.out 2>> $log.err  &
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

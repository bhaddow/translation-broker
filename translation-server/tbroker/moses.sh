#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids
port=8080
model_dir=/disk3/bhaddow/experiments/demo/binarised
configs="$model_dir/moses.ini.en $model_dir/moses.ini.de"
mosesserver=/disk3/bhaddow/experiments/demo/dist/1796c3b/bin/mosesserver
mosesargs="-search-algorithm 1 -cube-pruning-pop-limit 500 -s 500 -persistent-cache-size 250000"
logdir=/disk4/translation-server/logs
export LD_LIBRARY_PATH=/disk4/boost/lib:$LD_LIBRARY_PATH

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
        kill -9 $pid
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

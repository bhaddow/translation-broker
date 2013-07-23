#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids.accept.compact
port=8190
model_dir=/disk4/html/accept/models
configs="$model_dir/symantec-1009-13/moses.ini"
mosesserver=/disk3/bhaddow/moses/dist/c9687e3/bin/mosesserver
mosesargs="-mbr -persistent-cache-size 250000"
logdir=/disk4/translation-server/logs.accept
export LD_LIBRARY_PATH=/disk4/boost/lib:$LD_LIBRARY_PATH

ulimit -c unlimited

start() {
    echo "Starting moses servers"
    if [ -f $pidfile ]; then
        echo >&2 "Error: moses already running"
        exit 1
    fi
    for config in $configs; do
        log=$logdir/mosesserver.compact.$port
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

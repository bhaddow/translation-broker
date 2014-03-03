#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids.eub
port=8280
mosesserver=/disk3/bhaddow/moses/dist/1796c3b/bin/mosesserver
logdir=/disk4/translation-server/logs.eub
mosesargs="-search-algorithm 1 -cube-pruning-pop-limit 500 -s 500"
#mosesargs=
export LD_LIBRARY_PATH=/disk4/boost/lib:$LD_LIBRARY_PATH

configs="$model_dir/symantec-baseline.en-fr/binarised/moses.ini"

ulimit -c unlimited

start() {
    echo "Starting moses servers"
    if [ -f $pidfile ]; then
        echo >&2 "Error: moses already running"
        exit 1
    fi
    for config in $configs; do
        log=$logdir/mosesserver.$port
        echo "Starting at `date`" >> $log.err
        nohup $mosesserver $mosesargs  -f $config --server-port $port >>$log.out 2>> $log.err &
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

#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids.accept
port=8180
model_dir=/disk4/html/accept/models
configs="$model_dir/symantec-baseline.en-fr/binarised/moses.ini $model_dir/symantec-baseline.en-de/binarised/moses.ini $model_dir/symantec-baseline.en-ja/binarised/moses.ini $model_dir/symantec-baseline.fr-en/binarised/moses.ini $model_dir/twb-baseline.en-fr/binarised/moses.ini $model_dir/twb-baseline.fr-en/binarised/moses.ini $model_dir/twb-baseline-2013.en-fr/binarised/moses.ini $model_dir/twb-baseline-2013.fr-en/binarised/moses.ini"
mosesserver=/disk3/bhaddow/moses/dist/1796c3b/bin/mosesserver
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

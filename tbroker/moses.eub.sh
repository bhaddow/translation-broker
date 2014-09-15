#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids.eub
port=8280
mosesserver=/disk3/bhaddow/moses/dist/1796c3b/bin/mosesserver
logdir=/disk4/translation-server/logs.eub
mosesargs=" -use-alignment-info -mbr -persistent-cache-size 250000"
model_dir=/disk4/html/eubridge/models
#configs="$model_dir/enasr-fr/moses.ini $model_dir/enasr-es/moses.ini $model_dir/enasr-de/moses.ini $model_dir/enasr-en/moses.ini"
configs="$model_dir/enasr-en/moses.ini"

#mosesargs=
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

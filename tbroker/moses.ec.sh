#!/bin/sh

#
# Starts/stops the moses servers
#

pidfile=/disk4/translation-server/pids.ec
port=9080
mosesserver=/disk3/bhaddow/moses/dist/1796c3b/bin/mosesserver
logdir=/disk4/translation-server/logs.ec
mosesargs="-search-algorithm 1 -cube-pruning-pop-limit 500 -s 500"
#mosesargs=
export LD_LIBRARY_PATH=/disk4/boost/lib:$LD_LIBRARY_PATH

# fr-en it-en
#configs="/disk4/webtrans-models/acquis/moses.weight-reused.ini.9 /disk4/webtrans-models/acquis/moses.weight-reused.ini.11"
# fr-en it-en nl-el de-fr en-lt hu-ro es-en
configs="/disk4/webtrans-models/acquis-truecased/moses.weight-reused.ini.9 /disk4/webtrans-models/acquis-truecased/moses.weight-reused.ini.11 /disk4/webtrans-models/acquis-truecased/moses.weight-reused.ini.120 /disk4/webtrans-models/acquis-truecased/moses.weight-reused.ini.192 /disk4/webtrans-models/acquis-truecased/moses.weight-reused.ini.253 /disk4/webtrans-models/acquis-truecased/moses.weight-reused.ini.389 /disk4/webtrans-models/acquis-truecased/moses.weight-reused.ini.6"

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

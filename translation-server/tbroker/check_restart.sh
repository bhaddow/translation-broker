#!/bin/sh

#
# Check processes are running, and restart if necessary
#

tserver_dir=/disk4/translation-server/tbroker

check() {
    port=$1
    expected=$2

    actual=`ps -ef | grep mosesserver | grep -c $port`
    #echo $expected $actual
    if [ $expected -gt $actual ]; then
        return 1
    else
        return 0
    fi
}

restart_server() {
    $1 stop
    $1 start
}

if  !  check 808 4 ; then
    echo "Moses prod server missing"
    restart_server $tserver_dir/moses.sh prod
#else 
#    echo "Moses prod server ok"
fi

if ! check 908 7 ; then
    echo "Moses ec server missing"
    restart_server $tserver_dir/moses.ec.sh ec
#else
#    echo "Moses ec server ok"
fi


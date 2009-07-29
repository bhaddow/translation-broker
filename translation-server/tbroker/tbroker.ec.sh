#!/bin/sh

here=`dirname $0`
classpath=`ls $here/lib/*`
classpath=`echo $classpath | sed -e 's/ /:/g'`
classpath=$here/config:$here/bin:$classpath
logdir=$here/../logs.ec

nohup java -classpath $classpath -Dlog4j.configuration=log4j.ec.properties  org.statmt.tbroker.Main config/thor-ec.xml >> $logdir/tbroker.out 2>> $logdir/tbroker.err &

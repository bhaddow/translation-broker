#!/bin/sh

here=`dirname $0`
classpath=`ls $here/lib/*`
classpath=`echo $classpath | sed -e 's/ /:/g'`
classpath=$here/config:$here/bin:$classpath
logdir=$here/../logs

nohup java -classpath $classpath -Dlog4j.configuration=log4j.prod.properties  org.statmt.tbroker.Main $here/config/thor-prod.xml >> $logdir/tbroker.out 2>> $logdir/tbroker.err &

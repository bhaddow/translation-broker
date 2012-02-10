#!/bin/sh

here=`dirname $0`
classpath=`ls $here/lib/*`
classpath=`echo $classpath | sed -e 's/ /:/g'`
classpath=$here/config:$here/bin:$classpath
logdir=$here/../logs.accept

nohup java -classpath $classpath -Dlog4j.configuration=log4j.accept.properties  org.statmt.tbroker.Main $here/config/thor-accept.xml >> $logdir/tbroker.out 2>> $logdir/tbroker.err &

#!/bin/sh

here=`dirname $0`
classpath=`ls $here/lib/*`
classpath=`echo $classpath | sed -e 's/ /:/g'`
classpath=$here/config:$here/bin:$classpath
logdir=$here/../logs.eub

#nohup java -classpath $classpath -Dlog4j.configuration=log4j.eub.properties  org.statmt.tbroker.Main $here/config/thor-eub.xml >> $logdir/tbroker.out 2>> $logdir/tbroker.err &
nohup java -classpath $classpath -Dlog4j.configuration=log4j.eub.properties  org.statmt.tbroker.Main $here/config/thor-eub-test.xml >> $logdir/tbroker.out 2>> $logdir/tbroker.err &

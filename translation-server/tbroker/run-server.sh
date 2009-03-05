#!/bin/sh

here=`dirname $0`
classpath=`ls $here/lib/*`
classpath=`echo $classpath | sed -e 's/ /:/g'`
classpath=$here/build/WEB-INF/classes:$classpath

java -classpath $classpath org.statmt.tbroker.Main $*

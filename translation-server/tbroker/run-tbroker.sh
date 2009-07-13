#!/bin/sh

here=`dirname $0`
classpath=`ls $here/lib/*`
classpath=`echo $classpath | sed -e 's/ /:/g'`
classpath=$here/config:$here/bin:$classpath

java -classpath $classpath org.statmt.tbroker.Main $*

#!/bin/bash

JAVA_HOME=/usr/local/jdk1.8.0_77
LOG_DIR=/data/logs/logger-test
LIB_DIR=/opt/apps/logger-test/lib
LIB_JARS=`ls $LIB_DIR | grep .jar | awk '{print "'$LIB_DIR'/"$0}' | tr "\n" ":"`

ACCESS_LOG_SWITCH=$1

JAVA_OPTS=" -Dport=8106 -DACCESS_LOG_SWITCH=$ACCESS_LOG_SWITCH "
JAVA_OPTS=" $JAVA_OPTS -d64 -Xmx4096m -Xms4096m -Xmn800m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m "
JAVA_OPTS=" $JAVA_OPTS -Xss256k -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -XX:+DisableExplicitGC "
JAVA_OPTS=" $JAVA_OPTS -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 "
JAVA_OPTS=" $JAVA_OPTS -Xloggc:$LOG_DIR/logger-test_gc.log -XX:+PrintGCDateStamps "
JAVA_OPTS=" $JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_DIR "
#JAVA_OPTS=" $JAVA_OPTS -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector "

nohup $JAVA_HOME/bin/java $JAVA_OPTS -classpath $LIB_JARS com.littlemine.test.LoggerApp > /dev/null 2>&1 &
#nohup /usr/local/java/bin/java $JAVA_OPTS -jar logger-test-1.0.0-SNAPSHOT.war > /dev/null 2>&1 &

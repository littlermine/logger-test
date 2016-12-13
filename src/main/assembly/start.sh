#!/bin/bash

LOG_DIR=/data/logs/test
LIB_DIR=/opt/apps/logger-test/lib
LIB_JARS=`ls $LIB_DIR | grep .jar | awk '{print "'$LIB_DIR'/"$0}' | tr "\n" ":"`

JAVA_OPTS=" -server -d64 -Xms2048m -Xmx2048m -Xmn400m -XX:PermSize=128m -XX:MaxPermSize=128m -Xss256k "
JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -XX:+DisableExplicitGC "
JAVA_OPTS=" $JAVA_OPTS -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 "
JAVA_OPTS=" $JAVA_OPTS -Xloggc:$LOG_DIR/test_gc.log -XX:+PrintGCDateStamps "
JAVA_OPTS=" $JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_DIR "
#JAVA_OPTS=" $JAVA_OPTS -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector "

#nohup /usr/local/java/bin/java $JAVA_OPTS -jar logger-test-1.0.0-SNAPSHOT.war > /dev/null 2>&1 &
nohup /usr/local/java/bin/java $JAVA_OPTS -classpath $LIB_JARS:$LIB_DIR com.littlemine.test.LoggerApp > /dev/null 2>&1 &

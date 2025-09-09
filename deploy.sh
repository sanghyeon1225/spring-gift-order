#!/bin/bash
BUILD_PATH=$(ls /home/ubuntu/*.jar | head -n 1)
JAR_NAME=$(basename $BUILD_PATH)

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 애플리케이션이 실행되지 않았습니다."
  sleep 1
else
  echo "> 현재 실행 중인 $CURRENT_PID 를 제거합니다."
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 애플리케이션 배포를 시작합니다. $JAR_NAME"
nohup java -jar $BUILD_PATH > application.log 2>&1 &

#!/bin/bash

PID=$(lsof -t -i:8080)

# 프로세스 종료
if [ -z "$PID" ]; then
  echo "No process is using port 8080."
else
  echo "Killing process with PID: $PID"
  kill -15 "$PID"

  # 직전 명령(프로세스 종료 명령)이 정상 동작했는지 확인
  if [ $? -eq 0 ]; then
    echo "Process $PID terminated successfully."
  else
    echo "Failed to terminate process $PID."
  fi
fi

JAR_FILE=$(ls /home/ubuntu/app/*.jar | head -n 1)

log "JAR 파일 실행: $JAR_FILE"

# 애플리케이션 로그 파일 설정
APP_LOG_DIR="/home/ubuntu/app/logs"
APP_LOG_FILE="$APP_LOG_DIR/application-$(date +%Y%m%d-%H%M%S).log"

log "애플리케이션 로그 파일: $APP_LOG_FILE"

sudo nohup java \
    -Dspring.profiles.active=dev \
    -Duser.timezone=Asia/Seoul \
    -Dserver.port=8080 \
    -Ddd.service=WellMeet-Backend \
    -Ddd.env=dev \
    -jar "$JAR_FILE" > "$APP_LOG_FILE" 2>&1 &

log "애플리케이션이 백그라운드에서 실행되었습니다."
log "로그 확인: tail -f $APP_LOG_FILE"
log "=== 배포 완료 ==="

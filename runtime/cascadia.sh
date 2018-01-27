#!/bin/bash
####
# Copyright (c) 2018 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

TIMESTAMP=`date +%Y%m%d-%H%M%S`

# http://www.ostricher.com/2014/10/the-right-way-to-get-the-directory-of-a-bash-script/
CASCADIA_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

CASCADIA_PID_FILE="$CASCADIA_HOME/cascadia.PID"

case "$1" in
  start)
  if [ -f "$CASCADIA_PID_FILE" ]; then
    CASCADIA_PID=`cat $CASCADIA_PID_FILE`
    if [ -d "/proc/$CASCADIA_PID" ]; then
      echo "Cascadia process $CASCADIA_PID is already running!"
      exit 1
    else
      echo "Cascadia is removing old cascadia.PID file."
      rm "$CASCADIA_PID_FILE"
    fi
  fi
  
  # log setup
  CASCADIA_LOGS_DIR="$CASCADIA_HOME/logs"
  mkdir -p $CASCADIA_LOGS_DIR
  CASCADIA_LOG="$CASCADIA_LOGS_DIR/cascadia-$TIMESTAMP.log"
  touch $CASCADIA_LOG
  rm -f "$CASCADIA_HOME/cascadia.log"
  ln -s $CASCADIA_LOG "$CASCADIA_HOME/cascadia.log"
  
  # bundles.info location
  CASCADIA_BUNDLES_INFO="file://$CASCADIA_HOME/eclipse/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info"
  
  # launch eclipse
  java \
   -Dorg.eclipse.equinox.simpleconfigurator.configUrl=$CASCADIA_BUNDLES_INFO \
   -jar $CASCADIA_HOME/eclipse/plugins/org.eclipse.equinox.launcher_1.3.201.v20161025-1711.jar \
   -consoleLog -clean \
   -data $CASCADIA_HOME/data/eclipse-workspace \
   -vmargs \
   -Xms64m -Xmx512m \
   1>> $CASCADIA_LOG 2>&1 &
  
  CASCADIA_PID=$!
  
  echo "$CASCADIA_PID" > $CASCADIA_PID_FILE
  echo "Cascadia process: $CASCADIA_PID" >> $CASCADIA_LOG
  echo "Cascadia process: $CASCADIA_PID, log: ./cascadia.log -> $CASCADIA_LOG"
  ;;
  stop)
  if [ -f "$CASCADIA_PID_FILE" ]; then
    CASCADIA_PID=`cat $CASCADIA_PID_FILE`
    rm $CASCADIA_PID_FILE
    echo "Cascadia process $CASCADIA_PID is now shutting down."
    tail -f "$CASCADIA_HOME/cascadia.log" --pid=$CASCADIA_PID
  else
    echo "Cascadia is already stopped or stopping."
  fi
  ;;
  status)
  if [ -f "$CASCADIA_PID_FILE" ]; then
    CASCADIA_PID=`cat $CASCADIA_PID_FILE`
    if [ -d "/proc/$CASCADIA_PID" ]; then
      echo "Cascadia process $CASCADIA_PID is apparently running."
    else
      rm "$CASCADIA_PID_FILE"
      echo "Cascadia is stopped (removed old cascadia.PID file)."
    fi
  else
    echo "Cascadia is stopped or stopping."
  fi
  ;;
  *)
  echo "Cascadia usage:"
  echo "  ./cascadia.sh status"
  echo "  ./cascadia.sh start"
  echo "  ./cascadia.sh stop"
  ;;
esac


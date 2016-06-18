#!/bin/bash

case "$1" in
start)
   kill -9 `cat /var/run/ioserver.pid`
   rm /var/run/index.pid
   node /usr/node/tmp/index.js &
   echo $!>/var/run/ioserver.pid
   ;;
stop)
   kill -9 `cat /var/run/ioserver.pid`
   rm /var/run/ioserver.pid
   ;;
status)
   if [ -e /var/run/ioserver.pid ]; then
      echo index.js is running, pid=`cat /var/run/ioserver.pid`
   else
      echo index.js is NOT running
      exit 1
   fi
   ;;
*)
   echo "Usage: $0 {start|stop|status}"
esac

exit 0
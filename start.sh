#!/bin/sh

SCRIPT_PATH="`dirname \"$0\"`"
if  [ ! -e "$SCRIPT_PATH/build/libs/currency-0.0.1-SNAPSHOT.jar" ]
then
   echo "Artifact not build, please execute build.sh first"
   exit 1
fi

java -jar build/libs/currency-0.0.1-SNAPSHOT.jar "$@"

#!/bin/sh

if [ $# -ne 1 -a $# -ne 2 ]; then
	echo "Usage: $0 <handler> [Json|@JsonFile]" >&2
	exit 1;
fi

EXORESP=`
	if [ $# = 2 ]; then
		/usr/bin/curl \
			-i -H "Accept: application/json" -X POST \
			-d "$2" \
			"http://localhost:8080/servlet/$1/" 2>/dev/null | tail -1
	else
		/usr/bin/curl \
			-i -H "Accept: application/json" -X POST \
			"http://localhost:8080/servlet/$1/" 2>/dev/null | tail -1
	fi;`

if [ ! -z "`echo "$EXORESP" | grep "eXO::reqID"`" ]; then
	EXOREQID="$EXORESP";
	echo "Got eXO::reqID. Long-polling..."
	while true; do
		EXORESP=`/usr/bin/curl \
			-i -H "Accept: application/json" -X POST \
			-d "eXO_data=${EXOREQID}" \
			"http://localhost:8080/servlet/$1/" 2>/dev/null | tail -1`
		if [ -z "`echo "$EXORESP" | grep "eXO::Processing"`" ]; then
			break;
		fi
	done;
fi;
if [ -z "`echo "$EXORESP" | grep "eXO::Status"`" ]; then
	echo "ERROR"
else
	echo "$EXORESP"
fi

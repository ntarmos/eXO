#!/bin/sh

if [ $# -ne 1 -a $# -ne 2 ]; then
	echo "Usage: $0 <handler> [Json|@JsonFile]" >&2
	exit 1;
fi

if [ $# = 2 ]; then
	/usr/bin/curl \
		-i -H "Accept: application/json" -X POST \
		-d "$2" \
		"http://localhost:8080/servlet/$1/" 2>/dev/null | tail -2
else
	/usr/bin/curl \
		-i -H "Accept: application/json" -X POST \
		"http://localhost:8080/servlet/$1/" 2>/dev/null | tail -2
fi
echo; echo

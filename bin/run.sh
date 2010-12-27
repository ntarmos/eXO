#!/bin/sh

PROJECT_ROOT=`dirname $0`/..
EXOROOT=$PROJECT_ROOT
MAINCLASS=ceid.netcins.frontend.CatalogFrontend

EXOCP=$EXOROOT/classes:$EXOROOT/jars/freepastry:$EXOROOT/jars/eXO
for jar in `find $EXOROOT/lib -type f -name '*.jar'`; do
	EXOCP=$EXOCP:$jar
done

java -cp $EXOCP $MAINCLASS $*

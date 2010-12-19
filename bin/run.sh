#!/bin/sh

PROJECT_ROOT=$PWD/../
EXOROOT=$PROJECT_ROOT
FPROOT=$PROJECT_ROOT/FreePastry.git/pastry
MAINCLASS=ceid.netcins.frontend.CatalogFrontend

EXOCP=$EXOROOT/classes:$EXOROOT/jars/freepastry
for jar in $EXOROOT/lib/*.jar; do
	EXOCP=$EXOCP:$jar
done
for jar in $FPROOT/lib/*.jar; do
	EXOCP=$EXOCP:$jar
done

java -cp $EXOCP $MAINCLASS $*

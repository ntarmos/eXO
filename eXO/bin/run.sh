#!/bin/sh

EXOROOT=/home/Work/Research/Code/eXO
FPROOT=/home/Work/Research/Code/FreePastry.git/pastry

EXOCP=$EXOROOT/classes:$EXOROOT/jars/freepastry
for jar in $EXOROOT/lib/*.jar; do
	EXOCP=$EXOCP:$jar
done
for jar in $FPROOT/lib/*.jar; do
	EXOCP=$EXOCP:$jar
done

java -cp $EXOCP ceid.netcins.simulator.SimMain $*

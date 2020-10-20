#!/bin/bash
SOURCES=$(ls src/*.cl)  # load ALL .cl files within this folder
INTERP="cool"     # path to interpreter executable

TMP_SRC=".combined.cl"
LOGFILE=".log"

if [ $# -lt 1 ]; then
    echo "Usage: ./run.sh <input file>"
    echo ""
    echo "Runs the input file(s) through the interpreter, then drops the interactive shell. Make sure ALL the files are all newline terminated (i.e they have an empy final line)." | fold -w 80 -s
    echo "If an lexical/syntax/semantic error occurs, press enter one more time to find out from which file the error originated" | fold -w 80 -s
    exit
fi

# this may seem like a hack. That's because it is.
cat $SOURCES > $TMP_SRC
cat $@ - | $INTERP $TMP_SRC | tee $LOGFILE

if [[ $(grep "ERROR:" $LOGFILE) ]]; then
    LINE=$(tail -n 1 $LOGFILE | grep -oE "[0-9]*")
    for f in $SOURCES; do
        FLINE=$(wc -l $f | cut -d" " -f1)
        DIFF=$(( LINE - FLINE ))
        if [ "$DIFF" -le "0" ]; then
            break
        else
            LINE=$DIFF
        fi
    done
    echo "###################"
    echo "Error is in file $f on line $LINE"
fi
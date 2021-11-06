###
# @Author: Yupeng Hou
# @Id: a1783922
# @Semester: 2
# @Year: 2021
# @Assignment Number: 3
# @LastEditors: Yupeng Hou
###

#!/bin/bash

echo "Start testing"
timeArray=(0 0 0 0 0)

for j in {1..6}; do
    for k in 1 2 3 4 5; do
        timestamp=$(date +%s%3N)
        timeArray[$k]=$(expr $timestamp % 50)
    done
    for n in {0..49}; do
        case $n in
        ${timeArray[0]})
            java Acceptor -1 &
            ;;
        ${timeArray[1]} | ${timeArray[2]} | ${timeArray[3]} | ${timeArray[4]})
            java Acceptor 30 &
            ;;
        *)
            java Acceptor 2 &
            ;;
        esac
        echo Acceptor $n
    done
    process_id=0
    if test $j -gt 1; then
        # echo okey &
        # if ./java Proposer $(expr $j \* 50) & | grep -q 'completed';
        # then
        #     echo 'One round finished'.
        #     fi
        echo "Opening Proposer"
        Output = $(java Proposer $(expr $j \* 50) &)
        echo "Proposer started"
        process_id=$!
        echo $Output
    fi
    if test $process_id -gt 0; then
        wait $process_id
    fi
    echo "One round finished."
    sleep 3
done

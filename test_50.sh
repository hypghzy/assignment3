###
# @Author: Yupeng Hou
# @Id: a1783922
# @Semester: 2
# @Year: 2021
# @Assignment Number: 3
# @LastEditors: Yupeng Hou
###

#!/bin/bash

javac *.java
echo "Start testing"
for noUse in {1..10}; do
    java PublicServices &

    # every 50 as a round, 6 rounds
    for j in {1..12}; do
        for n in {0..49}; do
            timestamp=$(date +%s%3N)
            condition=$(expr $timestamp % 4)
            case $condition in
            0)
                java Acceptor -1 &
                ;;
            1)
                java Acceptor 30 &
                ;;
            2 | 3)
                java Acceptor 2 &
                ;;
            esac
            # echo Acceptor $n
        done
        if test $j -gt 1; then
            java Proposer $(expr $j \* 50)
            echo "One round finished."
        fi
        sleep 3
    done

    sleep 4
    pkill -f java

done

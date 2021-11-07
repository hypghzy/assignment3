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
    echo "Start testing"
    java PublicServices &
    timeArray=(-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1)
    # every 50 as a round, 6 rounds
    for j in {1..6}; do
        # find 13 different random locations
        counter=0
        while true; do
            timestamp=$(date +%s%3N)
            timePoint=$(expr $timestamp % 50)
            if test $counter -eq 13; then
                break
            fi

            if [[ ! " ${timeArray[*]} " =~ " ${timePoint} " ]]; then
                timeArray[$counter]=$timePoint
                counter=$(expr $counter + 1)
            else
                continue
            fi
        done

        # start the acceptors
        # 5 dead servers with 8 lazy servers
        for n in {0..49}; do
            case $n in
            ${timeArray[0]} | ${timeArray[1]} | ${timeArray[2]} | ${timeArray[3]} | ${timeArray[4]})
                java Acceptor -1 &
                ;;
            ${timeArray[5]} | ${timeArray[6]} | ${timeArray[7]} | ${timeArray[8]} | ${timeArray[9]} | ${timeArray[10]} | ${timeArray[11]} | ${timeArray[12]})
                java Acceptor 30 &
                ;;
            *)
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

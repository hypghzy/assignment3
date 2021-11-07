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

    for j in {1..12}; do
        counter=0
        for n in {0..49}; do
            java Acceptor &
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

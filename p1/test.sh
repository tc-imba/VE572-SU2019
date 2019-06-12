#!/bin/bash
for n in {A..A}; do
    echo $n
    tar -xvf $n.tar.gz
    #rm $n.tar.gz
done

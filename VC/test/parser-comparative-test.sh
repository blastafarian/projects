#! /usr/local/bin/bash

# Brendon Kwan 
# September 2006

# This shell script compares each of the .vcu files generated by my 
# VC parser with the corresponding .sol file given by the lecturer

for i in `ls Parser/*.vc` 
do
    echo $i:
    b=Parser/`basename $i .vc`
    diff ${i}u $b.sol
done 

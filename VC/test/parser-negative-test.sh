#! /usr/local/bin/bash

# Brendon Kwan
# September 2006
#
# This shell script tests for the INcorrectness of 
# my VC parser.

for i in `ls Parser/*vc` 
do
    echo $i :

    # parse the vc source code and unparse it to a .vcu file
    java VC.vc -u ${i}u $i > tmp

    # parse the .vcu file just generated and unparse it to a .vcuu file
    java VC.vc -u ${i}uu ${i}u > tmp

    # clean up ...
    rm tmp

    # compare our .vcuu files with our .vcu files
    # If they are different: my program is wrong. 
    # If they are the same: my program may be wrong, may be right.
    diff ${i}u ${i}uu
done

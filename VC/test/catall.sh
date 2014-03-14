#! /usr/local/bin/bash
for i in `ls Parser/*.vc`
do
    echo '----------------------------------'
    echo '--         ' $i '             --'
    echo '----------------------------------'
    cat $i
done


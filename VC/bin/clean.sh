#!/bin/bash
# Remove the working copy of VC

. ./variables.sh

echo "Removing the working copy of the VC"

if [ -d ${VC_WORKING_COPY} ]
then
	rm -rf ${VC_WORKING_COPY}
fi

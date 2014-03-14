#!/bin/bash
# Check out VC

. ./clean.sh

echo "Checking out VC"

svn checkout --quiet ${VC_REPO_URL} ${VC_WORKING_COPY}



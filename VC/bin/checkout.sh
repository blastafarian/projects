#!/bin/bash
# Check out VC

. ./clean.sh

echo "Checking out VC"

mkdir -p ${VC_WORKING_COPY}
cd ${VC_WORKING_COPY}
git init
git remote add ${GIT_REPO_ALIAS} ${GIT_REPO_URL}
git fetch ${GIT_REPO_ALIAS}
git merge ${GIT_REPO_ALIAS}/${GIT_BRANCH_NAME}


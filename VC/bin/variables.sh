#!/bin/bash
# Variables required to clean, checkout and compile

echo "Setting environment variables"

export GIT_REPO_ALIAS=projects
export GIT_REPO_URL=git@github.com:blastafarian/projects.git
export GIT_BRANCH_NAME=master

export VC_WORKING_COPY=/tmp/VC
export SRC_WORKING_COPY=/tmp/VC/VC/src
export BUILD_SCRIPTS_WORKING_COPY=/tmp/VC/VC/bin

export COMPILE_DIR=/tmp/VC/VC/compiled


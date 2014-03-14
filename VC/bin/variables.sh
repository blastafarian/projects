#!/bin/bash
# Variables required to clean, checkout and compile

echo "Setting environment variables"

export ROOT_REPO_DIR=/opt/apache/svn/repo
export VC_REPO_URL=file://${ROOT_REPO_DIR}/VC

export VC_WORKING_COPY=/tmp/VC
export SRC_WORKING_COPY=/tmp/VC/src
export BUILD_SCRIPTS_WORKING_COPY=/tmp/VC/bin

export COMPILE_DIR=/tmp/VC/compiled


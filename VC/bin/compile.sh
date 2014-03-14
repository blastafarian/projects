#!/bin/bash
# Builds the source code of VC

. ./checkout.sh

echo "Compiling the VC source code"

mkdir -p ${COMPILE_DIR}

javac -sourcepath ${SRC_WORKING_COPY} -d ${COMPILE_DIR} ${SRC_WORKING_COPY}/VC/vc.java

#!/bin/bash

docker --version
docker build -t code-view client/ktor-client
docker tag code-view docker.pkg.github.com/e13mort/codeview/code-view:latest
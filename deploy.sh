#!/bin/bash

docker --version
docker login docker.pkg.github.com -u e13mort -p $DOCKER_PASSWORD
docker build -t code-view client/ktor-client
docker tag code-view docker.pkg.github.com/e13mort/codeview/code-view:latest
docker push docker.pkg.github.com/e13mort/codeview/code-view:latest
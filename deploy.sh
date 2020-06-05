#!/bin/bash

docker login docker.pkg.github.com -u e13mort -p $DOCKER_PASSWORD
docker push docker.pkg.github.com/e13mort/codeview/code-view:latest
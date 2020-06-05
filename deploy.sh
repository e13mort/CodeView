#!/bin/bash

docker login -u e13mort -p $DOCKER_PASSWORD
docker push e13mort/codeview:latest
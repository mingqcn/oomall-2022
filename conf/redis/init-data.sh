#!/bin/bash
cd /root/oomall-2022
git pull
docker exec $(docker container ls -aq -f name=redis.*) redis-cli -a 123456 flushdb

#!/bin/bash
cd /root/oomall-2022
git pull
docker exec -it $(docker container ls -aq -f name=mongo.*) mongorestore -u demouser -p 123456 --authenticationDatabase oomall -d oomall --dir /root/oomall-2022/conf/mongo/oomall --drop
#!/bin/bash
cd /root/oomall-2022
git pull
cd /root/oomall-2022/core
mvn clean install -Dmaven.test.skip=true
cd /root/oomall-2022/goods
mvn clean pre-integration-test -Dmaven.test.skip=true
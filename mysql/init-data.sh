#!/bin/bash

cd /root/oomall-2022
git pull

for M in 'payment' 'shop' 'goods' 'alipay' 'wechatpay' 'region'
do
  docker exec -i $(docker container ls -aq -f name=mysql.*) mysql -udemouser -p123456 -D $M < /root/oomall-2022/mysql/sql/$M.sql
done
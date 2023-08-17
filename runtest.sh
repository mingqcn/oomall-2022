#!/bin/bash
## 将文件结尾从CRLF改为LF，解决了cd 错误问题
#$1为目录名
TIME=$(date "+%Y-%m-%d-%H-%M-%S")

cd /root/unit-test
if [ -d $TIME ];then
  rm -r $TIME
fi
mkdir $TIME

cd /root/oomall-2022
git pull

docker exec -it mongo mongorestore -u demouser -p 123456 --authenticationDatabase oomall -d oomall --dir /mongo/oomall --drop
docker exec redis redis-cli -a 123456 flushdb

cd core
mvn clean install surefire-report:report
mv target/site/jacoco /root/unit-test/$TIME/core
cp target/site/surefire-report.html /root/unit-test/$TIME/core-test.html
cp -r /root/site/images /root/unit-test/$TIME
cp -r /root/site/css /root/unit-test/$TIME


for M in 'payment' 'shop' 'goods' 'alipay' 'wechatpay' 'region'
do
  docker exec -i mysql mysql -udemouser -p123456 -D $M < /root/oomall-2022/mysql/sql/$M.sql
  docker exec redis redis-cli -a 123456 flushdb

  cd /root/oomall-2022/$M
  mvn clean surefire-report:report
  mv target/site/jacoco /root/unit-test/$TIME/$M
  #cp target/*.jar /root/unit-test/$TIME
  cp target/site/surefire-report.html /root/unit-test/$TIME/$M-test.html
done

mv /root/unit-test/console.log /root/unit-test/$TIME
cd /root/unit-test
if [ -d latest ];then
  rm -r latest
fi

mkdir latest
cp -r  /root/unit-test/$TIME/* latest


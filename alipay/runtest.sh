#!/bin/bash
## 将文件结尾从CRLF改为LF，解决了cd 错误问题
#$1为目录名
TIME=$(date "+%Y-%m-%d-%H-%M-%S")

cd /root/unit-test
if [ -d $TIME ];then
  rm -r $TIME
fi
mkdir $TIME
echo "****************************************************************************************************" >> /root/unit-test/$TIME/result.txt
echo " Test Result $(date "+%Y-%m-%d %H:%M:%S")" >> /root/unit-test/$TIME/result.txt
echo "****************************************************************************************************\n" >> /root/unit-test/$TIME/result.txt

cd /root/oomall-2022
git pull

docker exec -it mongo mongorestore -u demouser -p 123456 --authenticationDatabase oomall -d oomall --dir /mongo/oomall --drop

echo "*****************************************************************************************************" >> /root/unit-test/$TIME/result.txt
echo "                                                core" >> /root/unit-test/$TIME/result.txt
echo "*****************************************************************************************************\n" >> /root/unit-test/$TIME/result.txt

cd core
mvn clean install
mv target/site/jacoco /root/unit-test/$TIME/core
for FILE in `ls target/surefire-reports/*Test.txt`
do
  cat $FILE >> /root/unit-test/$TIME/result.txt
  echo "\n" >> /root/unit-test/$TIME/result.txt
done

for M in 'payment' 'shop'
do
echo "**************************************************************************************************" >> /root/unit-test/$TIME/result.txt
echo "                                                $M" >> /root/unit-test/$TIME/result.txt
echo "**************************************************************************************************" >> /root/unit-test/$TIME/result.txt
  docker exec -i mysql mysql -udemouser -p123456 -D $M < /root/oomall-2022/mysql/sql/$M.sql
  cd /root/oomall-2022/$M
  mvn clean test
  mv target/site/jacoco /root/unit-test/$TIME/$M
  #cp target/*.jar /root/unit-test/$TIME
  for FILE in `ls target/surefire-reports/*Test.txt`
  do
    cat $FILE >> /root/unit-test/$TIME/result.txt
    echo "\n" >> /root/unit-test/$TIME/result.txt
  done
done

mv /root/unit-test/console.log /root/unit-test/$TIME
cd /root/unit-test
if [ -d latest ];then
  rm -r latest
fi

mkdir latest
cp -r  /root/unit-test/$TIME/* latest


# OOMALL

2022 Tutorial Project for course "Object-Oriented Analysis and Design“ and "JavaEE Platform Technologies"

<br>2022-11-03：[API 1.2.0版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.0#/) 2022年第一版API<br>
2022-11-09：[API 1.2.1版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.1#/) 2022年第二版API<br>
2022-11-21：[API 1.2.2版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.2#/) 2022年第三版API<br>
2022-11-27：[API 1.2.3版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.3#/) 2022年第四版API<br>
2022-12-10：[API 1.2.4版](https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.2.4#/) 2022年第五版API<br>

## 测试结果
系统每天**0:00 4:00 8:00 12:00 16:00 20:00**会自动进行一次单元测试，这是最近一次[测试结果](http://121.36.2.235/unit-test/latest/)和[历史测试结果](http://121.36.2.235/unit-test/)
<br>系统每天**00:50 4:50 8:50 12:50 16:50 20:50**会自动进行一次集成测试，这是[测试结果](http://121.36.2.235/integration-test/public/), 目录是按照测试时间产生的，在目录下存在output.log文件，可以看到测试时的完整输出

## 工程编译，调试的顺序
所有module都依赖于core模块，先要将core安装到maven的本地仓库，才能编译运行其他模块。方法如下：
1. 首先将oomall下的pom.xml文件中除·<module>core</module>·以外的module注释掉，<br>
2. 在maven的中跑install phase，将core和oomall安装到maven的本地仓库<br>
3. 将oomall下的pom.xml文件中注释掉的部分修改回来<br>
4. 编译打包其他部分<br>
5. 以后修改了core的代码，只需要单独install core到maven本地仓库，无需重复上述步骤<br>

## Dao层的find和retrieve方法命名
参考 Spring JPA的命名规范
<table><thead><tr><th style="text-align:left"><div><div class="table-header"><br>Keyword</p></div></div></th><th style="text-align:left"><div><div class="table-header"><br>Sample</p></div></div></th><th style="text-align:left"><div><div class="table-header"><br>JPQL snippet</p></div></div></th></tr></thead><tbody><tr><td style="text-align:left"><div><div class="table-cell"><br>And</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByLastnameAndFirstname</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.lastname = ?1 and x.firstname = ?2</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>Or</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByLastnameOrFirstname</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.lastname = ?1 or x.firstname = ?2</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>Is,Equals</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByFirstname,
findByFirstnameIs,
findByFirstnameEquals</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.firstname = ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>Between</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByStartDateBetween</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.startDate between ?1 and ?2</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>LessThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeLessThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age &lt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>LessThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeLessThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age &lt;= ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>GreaterThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeGreaterThan</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age &gt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>GreaterThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeGreaterThanEqual</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age &gt;= ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>After</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByStartDateAfter</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.startDate &gt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>Before</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByStartDateBefore</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.startDate &lt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>IsNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeIsNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age is null</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>IsNotNull,NotNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAge(Is)NotNull</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age not null</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>Like</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByFirstnameLike</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.firstname like ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>NotLike</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByFirstnameNotLike</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.firstname not like ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>StartingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByFirstnameStartingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.firstname like ?1(parameter bound with appended&nbsp;%)</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>EndingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByFirstnameEndingWith</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.firstname like ?1(parameter bound with prepended&nbsp;%)</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>Containing</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByFirstnameContaining</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.firstname like ?1(parameter bound wrapped in&nbsp;%)</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>OrderBy</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeOrderByLastnameDesc</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age = ?1 order by x.lastname desc</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>Not</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByLastnameNot</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.lastname &lt;&gt; ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>In</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeIn(Collection&lt;Age&gt; ages)</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age in ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>NotIn</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByAgeNotIn(Collection&lt;Age&gt; ages)</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.age not in ?1</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>True</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByActiveTrue()</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.active = true</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>False</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByActiveFalse()</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where x.active = false</p></div></div></td></tr><tr><td style="text-align:left"><div><div class="table-cell"><br>IgnoreCase</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>findByFirstnameIgnoreCase</p></div></div></td><td style="text-align:left"><div><div class="table-cell"><br>… where UPPER(x.firstame) = UPPER(?1)</p></div></div></td></tr></tbody></table>

## 环境安装
### 安装docker
如果没有安装docker需要先安装docker，安装方法参考以下网页
[https://docs.docker.com/engine/install/ubuntu/
](https://docs.docker.com/engine/install/ubuntu/)

## docker swarm环境下项目的安装指南
### docker swarm安装
如果在docker swarm上部署，需要先安装docker swarm，参考以下网页
[https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/
](https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/)

### 安装MySQL镜像和服务
#### 在目标服务器上安装MySQL镜像
`docker pull mysql`

#### 更新目标服务器的label
为目标服务器定义label，方便docker swarm在创建Service时，将Service部署在目标服务器上，以下我们在node6上定义了一个label `server=mysql`<br>
`docker node update --label-add server=mysql node6`<br>

#### 在docker swarm中创建服务
MySQL的配置文件目录conf.d和数据库初始化脚本都在oomall-2022的mysql目录下，需要把这些文件拷贝到运行mysql的节点上，并映射到容器中<br>
用以下命令创建mysql<br>
docker service create --name mysql --constraint node.labels.server==mysql --mount type=bind,source=/root/oomall-2022/mysql/sql,destination=/sql,readonly --mount type=bind,source=/root/oomall-2022/mysql/conf.d,destination=/etc/mysql/conf.d,readonly  --network my-net -e MYSQL_ROOT_PASSWORD=123456  -d mysql:latest`<br>
其中`-e MYSQL_ROOT_PASSWORD=123456`是设定数据库root账户密码<br>

#### 在运行mysql服务的节点上运行sql脚本
看一下mysql的服务运行在哪台服务器<br>
`docker service ps mysql`<br>
切换到运行mysql服务的机器，看一下mysql容器在这台机器的container id，将容器的CONTAINER ID拷贝替换下述命令中[CONTAINER ID],用这个容器运行mysql的命令<br>
`docker exec -it [CONTAINER ID] mysql -uroot -p`<br>
用root账号登录mysql服务器，在运行起来的mysql命令行中用`source /sql/database.sql`建立oomall各模块数据库<br>

#### 分别初始化各模块的数据
以goods模块为例，用`use goods`切换数据库<br>
用`source /sql/goods.sql`插入初始数据
<br>其他模块数据库的安装类似

### 安装带布隆过滤器的Redis

#### 在目标服务器上安装Redis镜像
`docker pull redis/redis-stack-server:latest `
<br>在管理机上，更新目标服务器的label
<br>为目标服务器定义label，方便docker swarm在创建Service时，将Service部署在目标服务器上，以下我们在node5上定义了一个label `server=redis`
<br>`docker node update --label-add server=redis node5`

#### 在docker swarm中创建Redis服务
将机器上的Redis配置目录映射到docker容器中，并把redis的服务加入到my-net网络
<br>`docker service create --name redis --constraint node.labels.server==redis --mount type=bind,source=/root/oomall-2022/conf/redis,destination=/etc/redis,readonly --network my-net -e CONFFILE=/etc/redis/redis.conf  -d redis/redis-stack-server:latest `
<br>其中`-e CONFFILE=/etc/redis/redis.conf`是指定redis的配置文件，在配置文件中我们设定了redis的连接密码为123456, redis的配置文件目录redis映射到容器中

#### 在运行redis服务的节点上查看redis的服务是否正常运行
看一下redis的服务运行在哪台服务器
<br>`docker service ps redis`
<br>切换到运行redis服务的机器，看一下redis容器在这台机器的container id，将容器的CONTAINER ID拷贝替换下述命令中[CONTAINER ID],用这个容器运行redis的命令行工具redis-cli
<br>`docker exec -it [CONTAINER ID] redis-cli`
<br>进入redis-cli后，先运行下面的命令输入密码
<br>`auth 123456`
<br>再测试Bloom过滤器是否正常
`BF.ADD testFilter hello`
`BF.EXISTS testFilter hello`
<br>如果均返回(integer) 1则可以正常使用redis了

### 安装Mongo镜像
#### 在目标服务器上安装Mongo镜像
`docker pull mongo:latest `
<br>更新目标服务器的label, 为目标服务器定义label，方便docker swarm在创建Service时，将Service部署在目标服务器上，以下我们在node4上定义了一个label `server=mongo`
<br>`docker node update --label-add server=mongo node4`

#### 在docker swarm中创建Mongo服务
将机器上的Redis配置目录映射到docker容器中，并把mongo的服务加入到my-net网络
<br>`docker service create --name mongo --constraint node.labels.server==mongo --network my-net  --mount type=bind,source=/root/oomall-2022/conf/mongo,destination=/mongo  -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=123456  -d mongo:latest mongod --auth `
<br>其中` -e MONGO_INITDB_ROOT_USERNAME=root`是默认的用户名 `-e MONGO_INITDB_ROOT_PASSWORD=123456`是连接密码为123456

#### 创建mongo的用户
在运行mongo服务的节点上查看mongo的服务是否正常运行
<br>看一下mongo的服务运行在哪台服务器
<br>`docker service ps mongo`
<br>切换到运行mongo服务的机器，看一下mongo容器在这台机器的container id，将容器的CONTAINER ID拷贝替换下述命令中[CONTAINER ID],用这个容器运行mongo的命令行工具mongosh
<br>`docker exec -it [CONTAINER ID] mongosh -u root -p 123456`
<br>进入mongosh后，先运行下面的命令切换database
<br>`use oomall`
<br>再在oomall建立demouser用户，给用户赋予读写和数据库管理员的角色
<br>`db.createUser({user:"demouser",pwd:"123456",roles:[{role:"readWrite",db:"oomall"},{role:"dbAdmin",db:"oomall"}]})`
<br>如果均返回{ok:1}则可以用demouser用户正常使用mongo的oomall数据库了
`docker exec -it [CONTAINER ID] mongosh -u demouser -p 123456 --authenticationDatabase oomall`

#### restore mongo的数据库
`docker exec -it [CONTAINER ID] mongorestore -u demouser -p 123456 --authenticationDatabase oomall -d oomall --dir /mongo/oomall --drop`
<br>此命令会先删除所有数据再用户mongo/oomall下的数据恢复数据库

### 安装Rocketmq
#### 在node2 安装rocketmq的nameserver和dashboard，在node3安装rocketmq的broker
在管理机上执行以下命令
<br>`docker node update --label-add server=rocketmq-namesrv node2`
<br>`docker node update --label-add server=rocketmq-broker node3`
<br>在node2上pull docker镜像
<br>`docker pull apache/rocketmq`
<br>`docker pull apacherocketmq/rocketmq-dashboard:latest`
<br>在node3上pull docker镜像
<br>`docker pull apache/rocketmq`
<br>将oomall/conf/rocketmq/broker.conf文件拷贝到/root/rocketmq目录下

#### 创建RocketMQ的NameServer 服务
`docker service create --name rocketmq-namesrv --constraint node.labels.server==rocketmq-namesrv  --network my-net  -d apache/rocketmq ./mqnamesrv`

#### 创建RocketMQ的Broker 服务
`docker service create --name rocketmq-broker --constraint node.labels.server==rocketmq-broker --mount type=bind,source=/root/rocketmq,destination=/rocketmq  --network my-net  -d apache/rocketmq ./mqbroker -n rocketmq-namesrv:9876 -c /rocketmq/broker.conf`

#### 安装Rocket Dashboard
`docker service create --name rocketmq-dashboard --constraint node.labels.server==rocketmq-namesrv  --network my-net  -d -e "JAVA_OPTS=-Drocketmq.namesrv.addr=rocketmq-namesrv:9876" -p 8100:8080 -t apacherocketmq/rocketmq-dashboard:latest`
<br>然后访问集群的任何的8100端口就可以看到服务器了

### 安装oomall的模块
选择一个节点安装打包oomall-2022，例如node2
<br>`git clone http://git.xmu.edu.cn/mingqiu/oomall-2022.git`
<br>修改oomall-2022下的pom文件，将除core以外的模块删除
<br>在oomall-2022目录用以下命令安装core和oomall的pom
<br>`mvn clean install`
<br>如果第二次打包不用重复上述过程，直接在core目录下运行
<br>`mvn clean install`
<br>在oomall-2022目录下运行
<br>`git checkout pom.xml`
<br>将pom.xml恢复原样,
<br>在goods目录下运行
<br>`mvn clean pre-integration-test -Dmaven.test.skip=true`
<br>在node2的/root/上建立logs目录
<br>在管理机上创建服务
<br>`docker node update --label-add server=goods node2`
<br>`docker service create --name goods  --network my-net --constraint node.labels.server==goods --publish published=8080,target=8080 --mount type=bind,source=/root/logs,destination=/app/logs -d xmu-oomall/goods:0.0.1-SNAPSHOT`

### 安装nacos

#### 设定在node2 安装nacos
在管理机上执行
<br>`docker node update --label-add server=nacos node2`
<br>在node2上pull docker镜像
<br>`docker pull nacos/nacos-server:v2.1.2`
 
#### 启动nacos
`docker service create --name nacos --constraint node.labels.server==nacos  --network my-net --publish published=8848,target=8848  -e MODE=standalone  -e PREFER_HOST_MODE=hostname -d nacos/nacos-server:v2.1.2`
<br>其中MODE表示用standalone模式启动， PREFER_HOST_MODE表示支持hostname方式，因为用的是swarn，需要用服务名查询
<br>设置集中配置的application.yaml
<br>在浏览器中访问http://[IP]:8848/nacos, IP为集群中任意一台服务器ip
<br>输入默认用户名/密码: nacos/nacos
<br>即可进入nacos的控制台
<br>在ConfigurationManagement->Configurations中增加一项配置Create Configuration
<br>Data Id的格式为 ${spring.application.name}.yaml, 如商品模块为goods-service.yaml，商铺模块为shop-service.yaml，支付模块为payment-service.yaml
<br>Group：为默认的DEFAULT_GROUP
<br>Format：选Yaml
<br>Configuration Content：将对应模块的application.yaml内容拷贝进来，注意不能有中文注释
<br>按publish即可


## docker环境下项目的安装指南

### 安装MySQL镜像和服务
#### 在目标服务器上安装MySQL镜像
MySQL的配置文件目录conf.d和数据库初始化脚本都在oomall-2022的mysql目录下，需要把这些文件拷贝到运行mysql的节点上，并映射到容器中
<br>用以下命令创建mysql
<br>docker run --name mysql  -v /root/oomall-2022/mysql/sql:/sql:ro -v /root/oomall-2022/mysql/conf.d:/etc/mysql/conf.d:ro -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456  -d mysql:latest`
<br>其中`-e MYSQL_ROOT_PASSWORD=123456`是设定数据库root账户密码, 3306端口绑定到主机端口

#### 在运行mysql的docker容器上运行sql脚本
`docker exec -it mysql mysql -uroot -p`
<br>用root账号登录mysql服务器，在运行起来的mysql命令行中用`source /sql/database.sql`建立oomall各模块数据库

#### 分别初始化各模块的数据
以goods模块为例，用`use goods`切换数据库
<br>用`source /sql/goods.sql`插入初始数据
<br>其他模块数据库的安装类似

### 安装带布隆过滤器的Redis

#### 在目标服务器上安装Redis镜像
将机器上的Redis配置目录映射到docker容器中
<br>`docker run --name redis -v /root/oomall-2022/conf/redis:/etc/redis:ro -p 6379:6379 -e CONFFILE=/etc/redis/redis.conf  -d redis/redis-stack-server:latest`
<br>其中`-e CONFFILE=/etc/redis/redis.conf`是指定redis的配置文件，在配置文件中我们设定了redis的连接密码为123456, redis的配置文件目录redis映射到容器中

#### 在运行redis的容器上查看redis的服务是否正常运行
`docker exec -it redis redis-cli`
<br>进入redis-cli后，先运行下面的命令输入密码
<br>`auth 123456`
<br>再测试Bloom过滤器是否正常
<br>`BF.ADD testFilter hello`
<br>`BF.EXISTS testFilter hello`
<br>如果均返回(integer) 1则可以正常使用redis了

### 安装Mongo镜像
#### 在目标服务器上安装Mongo镜像
将机器上的Redis配置目录映射到docker容器中
<br>`docker run --name mongo -v /root/oomall-2022/conf/mongo:/mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=123456  -d mongo:latest mongod --auth`
<br>其中` -e MONGO_INITDB_ROOT_USERNAME=root`是默认的用户名 `-e MONGO_INITDB_ROOT_PASSWORD=123456`是连接密码为123456

#### 创建mongo的用户
`docker exec -it mongo mongosh -u root -p 123456`
<br>进入mongosh后，先运行下面的命令切换database
<br>`use oomall`
<br>再在oomall建立demouser用户，给用户赋予读写和数据库管理员的角色
<br>`db.createUser({user:"demouser",pwd:"123456",roles:[{role:"readWrite",db:"oomall"},{role:"dbAdmin",db:"oomall"}]})`
<br>如果均返回{ok:1}则可以用demouser用户正常使用mongo的oomall数据库了
<br>`docker exec -it mongo mongosh -u demouser -p 123456 --authenticationDatabase oomall`

#### restore mongo的数据库
`docker exec -it mongo mongorestore -u demouser -p 123456 --authenticationDatabase oomall -d oomall --dir /mongo/oomall --drop`
<br>此命令会先删除所有数据再用户mongo/oomall下的数据恢复数据库

#### 创建RocketMQ的NameServer容器
`docker run -d --name rocketmq-namesrv -d -p 9876:9876 apache/rocketmq ./mqnamesrv`

#### 创建RocketMQ的Broker容器
修改原有的broker.conf文件，将其中的brokerIP1=rockermq-broker改为运行broker容器的IP。
<br>`docker run -d --name rocketmq-broker -d -p 10911:10911 -p 10909:10909 -v /home/mingqiu/oomall-2022/conf/rocketmq:/rocketmq apache/rocketmq ./mqbroker -n [IP]:9876 -c /rocketmq/broker.conf`
<br>其中IP为rocketmq nameserver的ip

#### 安装Rocket Dashboard容器
`docker run -d --name rocketmq-dashboard -d  -e "JAVA_OPTS=-Drocketmq.namesrv.addr=[IP]:9876" -p 8100:8080 -t apacherocketmq/rocketmq-dashboard:latest`
<br>其中IP为rocketmq nameserver的ip，然后访问该机器的8100端口就可以看到Rocketmq的dashboard了

### 安装oomall的模块
安装打包oomall-2022，例如node2
<br>`git clone http://git.xmu.edu.cn/mingqiu/oomall-2022.git`
<br>修改oomall-2022下的pom文件，将除core以外的模块删除
<br>在oomall-2022目录用以下命令安装core和oomall的pom
<br>`mvn clean install`
<br>如果第二次打包不用重复上述过程，直接在core目录下运行
<br>`mvn clean install`
<br>在oomall-2022目录下运行
<br>`git checkout pom.xml`
<br>将pom.xml恢复原样,
<br>在goods目录下运行
<br>`mvn clean pre-integration-test -Dmaven.test.skip=true`
<br>在node2的/root/上建立logs目录
<br>创建容器
<br>`docker run  --name goods  -p 8080:8080 -v /root/logs:/app/logs -d xmu-oomall/goods:0.0.1-SNAPSHOT`
<br>其中8080为模块的端口，注意每个模块的application.yaml中端口设置

### 安装nacos
`docker run --name nacos  -p 8848:8848 -e MODE=standalone  -e PREFER_HOST_MODE=hostname -d nacos/nacos-server:v2.1.2`
<br>其中MODE表示用standalone模式启动， PREFER_HOST_MODE表示支持hostname方式，因为用的是swarn，需要用服务名查询
<br>设置集中配置的application.yaml
<br>在浏览器中访问http://[IP]:8848/nacos, IP为服务器ip
<br>输入默认用户名/密码: nacos/nacos
<br>即可进入nacos的控制台
<br>在ConfigurationManagement->Configurations中增加一项配置Create Configuration
<br>Data Id的格式为 ${spring.application.name}.yaml, 如商品模块为goods-service.yaml，商铺模块为shop-service.yaml，支付模块为payment-service.yaml
<br>Group：为默认的DEFAULT_GROUP
<br>Format：选Yaml
<br>Configuration Content：将对应模块的application.yaml内容拷贝进来，注意不能有中文注释
<br>按publish即可

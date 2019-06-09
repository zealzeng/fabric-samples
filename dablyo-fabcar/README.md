
我在自己的1.4测试了一下， 我用两台机器

192.168.31.86 安装ca,peer0和couchdb

192.168.31.168 安装orderer.


代码我已上传到https://github.com/zealzeng/fabric-samples/tree/master/dablyo-fabcar


共有一个dablyo-docker-compose.yml


    168机器安装orderer

dablyo-startFabric.sh启动，对应使用basic-network的dablyo-start.sh, dablyo-docker-compose.yml， docker ps查看只有orderer启动。


    86的机器ca,peer0和couchdb

dablyo-startFabric.sh启动，对应使用basic-network的dablyo-start.sh, dablyo-docker-compose.yml， docker ps查看只有ca,peer0,couchdb, cli中自动channel和链码安装且被调用.

修改basic-network/connection.json, 进入javascript目录npm install -unsafe-perm, 之后调用node enrollAdmin.js, node registerUser.js, node query.js, node invoke.js, 都成功。


最后拷贝生成wallet私钥等到168机器, scp -r wallet root@192.168.31.168:/mnt/sda3/fabric-samples/fabcar/javascript


    168机器执行链码

    修改basic-network/connection.json, 进入javascript目录, npm install等， 执行node query.js, node invoke.js即可.

TIM截图20190328194428.png

很遗憾 ：( 。。。我执行成功了，即使里面存在不大正确的zte.xx.xx那些配置。。 看下是不是网络问题。防火墙什么，希望你能找到问题。。

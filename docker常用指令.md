### docker

#### docker 安装

```bash
$ sudo yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2
  
$ sudo yum-config-manager \
  --add-repo \
  https://download.docker.com/linux/centos/docker-ce.repo
$ sudo yum install docker-ce docker-ce-cli containerd.io
$ sudo systemctl start docker
# 将 docker 服务设置为开机启动
$ systemctl enable docker.service
# 查看服务是否开机启动
$ systemctl is-enabled docker

$ sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

$ sudo chmod +x /usr/local/bin/docker-compose
```

#### docker 常用指令

##### docker 网络操作

```bash
# 将运行中容器更新为自动启动
$ docker update --restart=always CONTAINER
# 创建一个名为 back-net 网络
$ docker network create back-net
# 将容器加入到 back-net 网络
$ docker network connect back-net CONTAINER
$ docker network inspect back-net

```
##### docker 镜像、容器操作

```bash
# 使用 Dockerfile 构建镜像
$ docker build -t zhongkerd/openjdk:8-jdk-alpine . 

# 登录docker images私服
$ docker login proxy.vvinfor.com:8086
$ docker tag zhongkerd/openjdk:8-jdk-alpine proxy.vvinfor.com:8086/zhongkerd/openjdk:8-jdk-alpine
$ docker push proxy.vvinfor.com:8086/zhongkerd/openjdk:8-jdk-alpine

# 设置容器退出后重启策略为自动启动
$ docker update --restart=always <CONTAINER ID>  

# 将所有运行中容器重启策略更新为自动启动
$ docker update --restart=always $(docker ps | awk '{ print $1}' | tail -n +2)

# 删除所有已退出容器
$ docker rm $(docker ps -qf status=exited)

# 删除所有镜像名或版本号为none的镜像
$ docker rmi $(docker images | grep "none" | awk '{print $3}')

# 为镜像 id 为 b4122e028b9e 的镜像 申明镜像名和版本号
$ docker tag b4122e028b9e zhangkai/yapi:v1

# 进入 69d1 容器
$ docker exec -it 69d1 /bin/bash

# 将现有容器打包为镜像
$ docker container export -o ./xxl-job-admin.docker xxl-job-admin
```

##### docker 容器和宿主机交互

```bash
# 将容器中文件拷贝至宿主机
$ docker cp nginx:/etc/nginx/nginx.conf ~/dockerApps/nginx/nginx.conf
```

##### docker 高级操作

```
# 端口映射
$ docker run -p
# 挂载数据卷
$ docker run -v
# 传递环境变量
$ docker run -e
$ docker inspect
$ netstat -luntp|grep 81
```
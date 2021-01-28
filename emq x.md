## EMQ X

### 简介

*EMQ X* (Erlang/Enterprise/Elastic MQTT Broker) 是基于 Erlang/OTP 平台开发的开源物联网 MQTT 消息服务器。

Erlang/OTP是出色的软实时 (Soft-Realtime)、低延时 (Low-Latency)、分布式 (Distributed)的语言平台。

MQTT 是轻量的 (Lightweight)、发布订阅模式 (PubSub) 的物联网消息协议。

EMQ X 设计目标是实现高可靠，并支持承载海量物联网终端的MQTT连接，支持在海量物联网设备间低延时消息路由:

1. 稳定承载大规模的 MQTT 客户端连接，单服务器节点支持50万到100万连接。
2. 分布式节点集群，快速低延时的消息路由，单集群支持1000万规模的路由。
3. 消息服务器内扩展，支持定制多种认证方式、高效存储消息到后端数据库。
4. 完整物联网协议支持，MQTT、MQTT-SN、CoAP、LwM2M、WebSocket 或私有协议支持。

### 安装说明

| server name  | hostname  | ip           |
| ------------ | --------- | ------------ |
| emq x server | K8s-node2 | 47.52.170.97 |

### 安装部署

#### 第一步，准备配置文件

#### 第二步，准备 docker-compose 文件

- 创建 docker-compose 文件 `/root/dockerApps/emqx/docker-compose.yml`

```yaml
version: '3.7'
services:
  emqx:
    image: emqx/emqx:v4.0.0
    container_name: emqx
    ports:
      - "1883:1883"
      - "8083:8083"
      - "8883:8883"
      - "8084:8084"
      - "18083:18083"
    extra_hosts:
      - "k8s-node1:47.75.196.191"
      - "k8s-node2:47.52.170.97"
```

#### 第三步，启动并配置集群

K8s-node1

```sh
$ cd /root/dockerApps/emqx/
# 执行 docker-compose 编排脚本
$ docker-compose up
```

### 连接器使用

#### MQTT Java 客户端库

`pom.xml`

```xml
<dependency>
  <groupId>org.eclipse.paho</groupId>
	<artifactId>org.eclipse.paho.client.mqttv3</artifactId>
	<version>1.2.2</version>
</dependency>
```



### 参考引用

[MQTT Java 客户端]: https://docs.emqx.cn/cn/broker/latest/development/java.html#paho-java-%E4%BD%BF%E7%94%A8%E7%A4%BA%E4%BE%8B	"MQTT Java 客户端"





- 大数据
- 云计算
- 物联网
- 人工智能





- 智慧办公

- 智慧楼宇

  

- 智能门禁

  - 远程开门

- 智能变电箱

  - 远程控制电路开关
  - 用电量统计

- 智能安防摄像头

  - 调用视频流查看
  - 安防预警消息接收
  - 行为分析（睡觉、抽烟）

- 智能环境检测设备

  - 温度
  - 湿度

- 智能音箱

  - 语义识别后推送至本平台

- 智能灯光

  - 控制亮度
  - 控制日光模式



租户模式架构设计



zigbee 无线上网协议
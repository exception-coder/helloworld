## tdengine

### 简介

TDengine社区版是一开源版本，采用的是AGPL许可证，是一个处理中小规模的物联网数据平台。它具备高效处理物联网数据所需要的所有功能，包括:

- 类SQL查询语言来插入或查询数据
- 支持C/C++, Java(JDBC), Python, Go, RESTful, and Node.JS 等开发接口
- 通过TDengine Shell或Python/R/Matlab可做各种Ad Hoc查询分析
- 通过连续查询，支持基于滑动窗口的流式计算
- 引入超级表，让设备之间的数据聚合通过标签变得简单、灵活
- 内嵌消息队列，应用可订阅最新的数据
- 内嵌缓存机制，每台设备的最新状态或记录都可快速获得
- 无历史数据与实时数据之分，对应用而言，透明且完全一样
- **分布式架构，支持线性扩展**，以保证任何规模的数据量都可以处理
- **支持多副本，无单点故障**，以保证系统的高可用与高可靠

### 安装说明

因资源限制，暂时选用2台主机构建集群。生产遵守2n+1原则，3台起步

<font color="#f00">注意检查并打开TCP/UDP 端口6030-6042的访问权限</font>

<font color="#f00">注意文件路径及指令执行路径</font>

| server name         | hostname  | ip            |
| ------------------- | --------- | ------------- |
| tdengine 集群主节点 | K8s-node1 | 47.75.196.191 |
| tdengine 集群从节点 | K8s-node2 | 47.52.170.97  |

### 安装部署

#### 第一步，准备配置文件

编辑 tdengine 配置文件 `/root/dockerApps/tdengine/etc/taos/taos.cfg`

<font color="#f00">一定要修改的参数是firstEp和fqdn。在每个数据节点，firstEp需全部配置成一样，但fqdn一定要配置成其所在数据节点的值。其他参数可不做任何修改，除非你很清楚为什么要修改</font>

<font color="#f00">k8s-node1</font> `taos.cfg` 配置文件

```cfg
########################################################
#                                                      #
#                  TDengine Configuration              #
#   Any questions, please email support@taosdata.com   #
#                                                      #
########################################################

# first fully qualified domain name (FQDN) for TDengine system
 firstEp                   k8s-node1:6030

# local fully qualified domain name (FQDN)
 fqdn                      k8s-node1

# first port number for the connection (12 continuous UDP/TCP port number are used) 
# serverPort                6030

# log file's directory
# logDir                    /var/log/taos

# data file's directory
# dataDir                   /var/lib/taos

# temporary file's directory
# tempDir                   /tmp/

# the arbitrator's fully qualified domain name (FQDN) for TDengine system, for cluster only   
# arbitrator                arbitrator_hostname:6042     

# number of threads per CPU core
# numOfThreadsPerCore       1.0

# the proportion of total CPU cores available for query processing
# 2.0: the query threads will be set to double of the CPU cores.
# 1.0: all CPU cores are available for query processing [default].
# 0.5: only half of the CPU cores are available for query.
# 0.0: only one core available.
# tsRatioOfQueryCores       1.0

# number of management nodes in the system
# numOfMnodes               3

# enable/disable backuping vnode directory when removing vnode
# vnodeBak                  1

# enable/disable installation / usage report
# telemetryReporting        1

# enable/disable load balancing
# balance                   1

# role for dnode. 0 - any, 1 - mnode, 2 - dnode
# role                      0

# max timer control blocks
# maxTmrCtrl                512

# time interval of system monitor, seconds 
# monitorInterval           30

# number of seconds allowed for a dnode to be offline, for cluster only 
# offlineThreshold          8640000

# RPC re-try timer, millisecond
# rpcTimer                  300

# RPC maximum time for ack, seconds. 
# rpcMaxTime                600

# time interval of dnode status reporting to mnode, seconds, for cluster only 
# statusInterval            1

# time interval of heart beat from shell to dnode, seconds
# shellActivityTimer        3

# minimum sliding window time, milli-second
# minSlidingTime            10

# minimum time window, milli-second
# minIntervalTime           10

# maximum delay before launching a stream computation, milli-second
# maxStreamCompDelay        20000

# maximum delay before launching a stream computation for the first time, milli-second
# maxFirstStreamCompDelay   10000

# retry delay when a stream computation fails, milli-second
# retryStreamCompDelay      10

# the delayed time for launching a stream computation, from 0.1(default, 10% of whole computing time window) to 0.9
# streamCompDelayRatio      0.1

# max number of vgroups per db, 0 means configured automatically
# maxVgroupsPerDb           0

# max number of tables per vnode
# maxTablesPerVnode         1000000

# cache block size (Mbyte)
# cache                     16

# number of cache blocks per vnode
# blocks                    6

# number of days per DB file
# days                  10

# number of days to keep DB file
# keep                  3650

# minimum rows of records in file block
# minRows               100

# maximum rows of records in file block
# maxRows               4096

# the number of acknowledgments required for successful data writing
# quorum                1     

# enable/disable compression
# comp                  2

# write ahead log (WAL) level, 0: no wal; 1: write wal, but no fysnc; 2: write wal, and call fsync
# walLevel              1

# if walLevel is set to 2, the cycle of fsync being executed, if set to 0, fsync is called right away
# fsync                 3000

# number of replications, for cluster only 
# replica               1

# the compressed rpc message, option:
#  -1 (no compression)
#   0 (all message compressed),
# > 0 (rpc message body which larger than this value will be compressed)
# compressMsgSize       -1

# max length of an SQL
# maxSQLLength          65480

# the maximum number of records allowed for super table time sorting
# maxNumOfOrderedRes    100000

# system time zone
# timezone              Asia/Shanghai (CST, +0800)

# system locale
# locale                en_US.UTF-8

# default system charset
# charset               UTF-8

# max number of connections allowed in dnode
# maxShellConns         5000

# max number of connections allowed in client
# maxConnections        5000

# stop writing logs when the disk size of the log folder is less than this value
# minimalLogDirGB       0.1

# stop writing temporary files when the disk size of the tmp folder is less than this value
# minimalTmpDirGB       0.1

# if disk free space is less than this value, taosd service exit directly within startup process
# minimalDataDirGB      0.1

# One mnode is equal to the number of vnode consumed
# mnodeEqualVnodeNum    4

# enbale/disable http service
# http                  1

# enable/disable system monitor 
# monitor               1

# enable/disable recording the SQL statements via restful interface
# httpEnableRecordSql   0

# number of threads used to process http requests
# httpMaxThreads        2

# maximum number of rows returned by the restful interface
# restfulRowLimit       10240

# The following parameter is used to limit the maximum number of lines in log files.
# max number of lines per log filters
# numOfLogLines         10000000

# enable/disable async log
# asyncLog              1

# time of keeping log files, days
# logKeepDays           0


# The following parameters are used for debug purpose only.
# debugFlag 8 bits mask: FILE-SCREEN-UNUSED-HeartBeat-DUMP-TRACE_WARN-ERROR
# 131: output warning and error 
# 135: output debug, warning and error
# 143: output trace, debug, warning and error to log
# 199: output debug, warning and error to both screen and file
# 207: output trace, debug, warning and error to both screen and file

# debug flag for all log type, take effect when non-zero value
# debugFlag             0

# debug flag for meta management messages
# mDebugFlag            135

# debug flag for dnode messages
# dDebugFlag            135

# debug flag for sync module
# sDebugFlag            135

# debug flag for WAL
# wDebugFlag            135

# debug flag for SDB
# sdbDebugFlag          135

# debug flag for RPC 
# rpcDebugFlag          131

# debug flag for TAOS TIMER
# tmrDebugFlag          131

# debug flag for TDengine client 
# cDebugFlag            131

# debug flag for JNI
# jniDebugFlag          131

# debug flag for storage
# uDebugFlag            131

# debug flag for http server
# httpDebugFlag         131

# debug flag for monitor
# monDebugFlag          131

# debug flag for query
# qDebugFlag            131

# debug flag for vnode
# vDebugFlag            131

# debug flag for TSDB
# tsdbDebugFlag         131

# debug flag for continue query
# cqDebugFlag           131

# enable/disable recording the SQL in taos client
# enableRecordSql    0

# generate core file when service crash
# enableCoreFile        1

# maximum display width of binary and nchar fields in the shell. The parts exceeding this limit will be hidden
# maxBinaryDisplayWidth 30

# enable/disable telemetry reporting
# telemetryReporting    1

# enable/disable stream (continuous query)
# stream                1

# in retrieve blocking model, only in 50% query threads will be used in query processing in dnode
# retrieveBlockingModel    0
```

<font color="#f00">k8s-node2</font> `taos.cfg` 配置文件

<font color="#f00">仅显示相对 k8s-node1 差异步分，其余部分保持一致</font>

```cfg

 firstEp               k8s-node1:6030
 
 fqdn                  k8s-node2
```

#### 第二步，准备 docker-compose 文件

- 创建 docker-compose 文件 `/root/dockerApps/tdengine/docker-compose.yml`

```yaml
version: '3.7'
services:
  tdengine:
    image: tdengine/tdengine:2.0.14.0
    container_name: tdengine
    ports:
      - "6030:6030"
      - "6035:6035"
      - "6041:6041"
      - "6030-6040:6030-6040/udp"
    volumes:
      - /root/dockerApps/tdengine/etc/taos:/etc/taos/
      - /root/dockerApps/tdengine/lib/taos:/var/lib/taos
      - /root/dockerApps/tdengine/log/taos:/var/log/taos
    extra_hosts:
      - "k8s-node1:47.75.196.191"
      - "k8s-node2:47.52.170.97"
```

#### 第三步，启动并配置集群

K8s-node1

```sh
$ cd /root/dockerApps/tdengine/
# 执行 docker-compose 编排脚本
$ docker-compose up
# 进入 tdengine 容器
$ docker exec -it tdengine /bin/bash
bash: warning: setlocale: LC_ALL: cannot change locale (en_US.UTF-8)
# 启动taos shell
$ taos
Welcome to the TDengine shell from Linux, Client Version:2.0.14.0
Copyright (c) 2020 by TAOS Data, Inc. All rights reserved.
# 查看数据节点
taos> show dnodes;
   id   |           end_point            | vnodes | cores  |   status   | role  |       create_time       |      offline reason      |
======================================================================================================================================
      1 | 47.75.196.191:6030             |      1 |      2 | ready      | any   | 2021-01-20 09:27:45.426 |                          |
Query OK, 1 row(s) in set (0.001822s)
# 将新数据节点的End Point (准备工作中第四步获知的) 添加进集群的EP列表
taos> create dnode "47.52.170.97:6030";
Query OK, 0 row(s) affected (0.012811s)

# 创建用户和密码
taos> CREATE USER dev PASS 'tddevP@ssw0RD';
Query OK, 0 row(s) affected (0.004429s)

# 显示所有用户
taos> SHOW USERS;
           name           | privilege |       create_time       |         account          |
============================================================================================
 _root                    | writable  | 2021-01-20 09:27:45.426 | root                     |
 dev                      | writable  | 2021-01-20 11:16:31.329 | root                     |
 monitor                  | writable  | 2021-01-20 09:27:45.426 | root                     |
 root                     | super     | 2021-01-20 09:27:45.426 | root                     |
Query OK, 4 row(s) in set (0.002643s)

# 删除用户 DROP USER <user_name>;

# 修改用户密码 ALTER USER <user_name> PASS <'password'>;

# 修改用户权限 ALTER USER <user_name> PRIVILEGE <write|read>;
```

K8s-node2

```sh
$ cd /root/dockerApps/tdengine/
```

### 连接器使用

#### RESTful Connector

##### curl 请求

```sh
# 获取授权码
curl http://k8s-node1:6041/rest/login/root/taosdata     
{"status":"succ","code":0,"desc":"/KfeAzX/f9na8qdtNZmtONryp201ma04bEl8LcvLUd7a8qdtNZmtOA=="}

# curl -H 'Authorization: Basic <TOKEN>' -d '<SQL>' <ip>:<PORT>/rest/sql 其中，TOKEN为{username}:{password}经过Base64编码之后的字符串，例如root:taosdata编码后为cm9vdDp0YW9zZGF0YQ==
curl -H 'Authorization: Basic ZGV2OnRkZGV2UEBzc3cwUkQ=' -d 'create database db' k8s-node1:6041/rest/sql
{"status":"succ","head":["affected_rows"],"data":[[0]],"rows":0} 
```

##### 基于 RESTful Connector 整合 feign

#### Java Connector

##### JDBC-JNI和JDBC-RESTful的对比

| 对比项                           | JDBC-JNI                             | JDBC-RESTful |
| -------------------------------- | ------------------------------------ | ------------ |
| 支持的操作系统                   | linux、windows                       | 全平台       |
| 是否需要安装 client              | 需要                                 | 不需要       |
| server 升级后是否需要升级 client | 需要                                 | 不需要       |
| 写入性能                         | JDBC-RESTful 是 JDBC-JNI 的 50%～90% |              |
| 查询性能                         | JDBC-RESTful 与 JDBC-JNI 没有差别    |              |

##### JDBC-JNI 连接方式使用

###### 安装本地函数库

使用 JDBC-JNI 的 driver，taos-jdbcdriver 驱动包时需要依赖系统对应的本地函数库。

taos.dll 在 windows 系统中安装完客户端之后，驱动包依赖的 taos.dll 文件会自动拷贝到系统默认搜索路径 C:/Windows/System32 下，同样无需要单独指定。

windows 系统客户端下载地址 ：

[windows客户端]: https://www.taosdata.com/cn/all-downloads/#TDengine-Windows-Client

###### Spring Boot 工程中使用 tdengine 实战

maven `pom.xml` 添加数据库驱动包

```xml
<dependency>
            <groupId>com.taosdata.jdbc</groupId>
            <artifactId>taos-jdbcdriver</artifactId>
            <version>2.0.18</version>
        </dependency>
```

使用 druid 连接池创建数据源

 数据源配置类 `DataSourceConfig`

```java
@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "tdengineDataSource")
    @ConfigurationProperties("spring.datasource.druid.tdengine.jni")
    public DataSource tdengineDataSource(){
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }

}
```

`application.yaml` 添加数据源相关属性配置

```yaml
spring:
  datasource:
    druid:
      tdengine:
        jni:
          driver-class-name: com.taosdata.jdbc.TSDBDriver
          url: jdbc:TAOS://k8s-node1:6030/db
          username: dev
          password: tddevP@ssw0RD
```





##### JDBC-RESTful

### 参考引用

[常见问题自查]: https://www.taosdata.com/cn/documentation20/faq/	"常见问题自查"


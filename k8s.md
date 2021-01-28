### 安装 cfssl

用于证书签发，k8s 生态大量通讯使用 ssl

```sh
# 依赖下载
$ wget https://pkg.cfssl.org/R1.2/cfssl_linux-amd64 -O /usr/bin/cfssl
$ wget https://pkg.cfssl.org/R1.2/cfssljson_linux-amd64 -O /usr/bin/cfssljson
$ wget https://pkg.cfssl.org/R1.2/cfssl-certinfo_linux-amd64 -O /usr/bin/cfssl-certinfo

# 统一授予可执行权限
$ chmod +x /usr/bin/cfssl*

#  创建证书
$ cd /opt/
$ mkdir certs
#  创建生成 CA 证书签名请求 (csr) 的 json 配置文件
$ cat /opt/certs/ca-csr.json
$ cfssl gencert -initca ca-csr.json | cfssljson -bare ca - 

```

- CN： Common Name,浏览器使用该字段验证网站是否合法，一般写的是域名、非常重要。浏览器使用该字段验证网站是否合法
- C：Country，国家
- ST：State，州，省
- L：Locality，地区，城市
- O：Organization Name，组织名称，公司名称
- OU：Organization Unit Name，组织单位名称，公司部门

`/opt/certs/ca-csr.json`

```json
{
    "CN": "k8s.cn",
    "hosts": [],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ],
    "ca": {
        "expiry": "175200h"
    }
}
```

### 部署 docker 环境

```sh
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

$ mkdir -p /data/docker
```

配置 docker

`/etc/docker/daemon.json`

```json
{
    "graph": "/data/docker",
    "storage-driver": "overlay2",
    "insecure-registries": ["registry.access.redhat.com"],
    "registry-mirrors": ["http://f1361db2.m.daocloud.io"],
    "bip": "172.7.57.1/24",
    "exec-opts": ["native.cgroupdriver=systemd"],
    "live-restore": true
                         
}
```

### 部署docker私有仓库 harbor



```sh
$ wget https://github.com/goharbor/harbor/releases/download/v2.1.1/harbor-offline-installer-v2.1.1.tgz

$ tar xf harbor-offline-installer-v2.1.1.tgz

$ harbor/install.sh

# docker 私服镜像提交测试
$ docker pull nginx:1.7.9
$ docker images
nginx                           1.7.9               84581e99d807        5 years ago         91.7MB
$ docker tag 84581e99d807 47.75.196.191/public/nginx:v1.7.9 
$ docker push 47.75.196.191/public/nginx:v1.7.9
$ docker login 47.75.196.191
Username: admin
Password: 
WARNING! Your password will be stored unencrypted in /root/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store

Login Succeeded

$ docker push 47.75.196.191/public/nginx:v1.7.9
```

```sh

```





### 安装部署 etcd

```sh
# 创建基于根证书的config配置文件
$ vi /opt/certs/ca-config.json
$ vi /opt/certs/etcd-peer-csr.json
$ cfssl gencert -initca etcd-peer-csr.json | cfssljson -bare ca -
$ cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=peer etcd-peer-csr.json | cfssljson -bare etcd-peer

# 创建 etcd 用户
$ useradd -s /sbin/nologin -M etcd
$ id etcd
uid=1001(etcd) gid=1001(etcd) groups=1001(etcd)

$ wget https://github.com/etcd-io/etcd/releases/download/v3.1.20/etcd-v3.1.20-linux-amd64.tar.gz

# 将下载的 etcd 解压到 /opt 目录下
$ tar xfv etcd-v3.1.20-linux-amd64.tar.gz -C /opt
$ cd /opt
# 修改解压后的目录名称
$ mv etcd-v3.1.20-linux-amd64 etcd-v3.1.20
# 设置软连接便于将来升级
$ ln -s /opt/etcd-v3.1.20/ /opt/etcd
$ mkdir -p /opt/etcd/certs /data/etcd /data/logs/etcd-server /data/etcd/etcd-server
$ cd /opt/etcd/certs
$ scp 47.106.123.217:/opt/certs/ca.pem .
$ scp 47.106.123.217:/opt/certs/etcd-peer.pem .
$ scp 47.106.123.217:/opt/certs/etcd-peer-key.pem .

# 为 etcd 目录配置所属用户和用户组
$ chown -R etcd.etcd /opt/etcd-v3.1.20/
$ chown -R etcd.etcd /opt/etcd/certs /data/etcd /data/logs/etcd-server /data/etcd/etcd-server

# 安装后台服务启动插件 supervisor
$ yum install supervisor -y
$ systemctl start supervisord
$ systemctl enable supervisord
$ rm -rf /data/etcd/etcd-server/member
$ vi /opt/etcd/etcd-server-startup.sh
$ chmod +x /opt/etcd/etcd-server-startup.sh
$ vi /etc/supervisord.d/etcd-server.ini
$ supervisorctl update
etcd-server01: added process group
$ supervisorctl status
$ tail -fn 200 /data/logs/etcd-server/etcd.stdout.log
$ netstat -luntp|grep etcd
tcp        0      0 172.18.57.245:2379      0.0.0.0:*               LISTEN      2295/./etcd         
tcp        0      0 127.0.0.1:2379          0.0.0.0:*               LISTEN      2295/./etcd         
tcp        0      0 172.18.57.245:2380      0.0.0.0:*               LISTEN      2295/./etcd  

# etcd 集群状态健康检查
$ /opt/etcd/etcdctl cluster-health
$ /opt/etcd/etcdctl member list


```

`/opt/certs/ca-config.json`

```json
{
    "signing":{
        "default":{
            "expiry": "175200h"
        },
         "profiles":{
             "server":{
                 "expiry":"175200h",
                 "usages":[
                     "signing",
                     "key encipherment",
                     "server auth"
                 ]
             },
			 "client":{
            "expiry":"175200h",
            "usages":[
                "signing",
                "key encipherment",
                "client auth"
            ]
        },
        "peer":{
            "expiry":"175200h",
                 "usages":[
                     "signing",
                     "key encipherment",
                     "server auth",
                     "client auth"
                 ]
        }
         }
    }
   
}

```

`etcd-peer-csr.json`

```json
{
    "CN": "k8s-etcd",
    "hosts": [
        "47.106.123.217",
        "47.75.196.191",
        "47.52.170.97"
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
```

节点1 `/opt/etcd/etcd-server-startup.sh`

```sh
#!/bin/sh
./etcd --name etcd-server01 \
--data-dir /data/etcd/etcd-server \
--listen-peer-urls https://172.18.57.245:2380 \
--listen-client-urls https://172.18.57.245:2379,http://127.0.0.1:2379 \
--quota-backend-bytes 8000000000 \
--initial-advertise-peer-urls https://47.106.123.217:2380 \
--advertise-client-urls https://47.106.123.217:2379,http://127.0.0.1:2379 \
--initial-cluster etcd-server01=https://47.106.123.217:2380,etcd-server02=https://47.75.196.191:2380,etcd-server03=https://47.52.170.97:2380 \
--ca-file ./certs/ca.pem \
--cert-file ./certs/etcd-peer.pem \
--key-file ./certs/etcd-peer-key.pem \
--client-cert-auth \
--trusted-ca-file ./certs/ca.pem \
--peer-ca-file ./certs/ca.pem \
--peer-cert-file ./certs/etcd-peer.pem \
--peer-key-file ./certs/etcd-peer-key.pem \
--peer-client-cert-auth \
--peer-trusted-ca-file ./certs/ca.pem \
--log-output stdout
```

节点2 `/opt/etcd/etcd-server-startup.sh`

```sh
#!/bin/sh
./etcd --name etcd-server02 \
--data-dir /data/etcd/etcd-server \
--listen-peer-urls https://172.31.10.244:2380 \
--listen-client-urls https://172.31.10.244:2379,http://127.0.0.1:2379 \
--quota-backend-bytes 8000000000 \
--initial-advertise-peer-urls https://47.75.196.191:2380 \
--advertise-client-urls https://47.75.196.191:2379,http://127.0.0.1:2379 \
--initial-cluster etcd-server01=https://47.106.123.217:2380,etcd-server02=https://47.75.196.191:2380,etcd-server03=https://47.52.170.97:2380 \
--ca-file ./certs/ca.pem \
--cert-file ./certs/etcd-peer.pem \
--key-file ./certs/etcd-peer-key.pem \
--client-cert-auth \
--trusted-ca-file ./certs/ca.pem \
--peer-ca-file ./certs/ca.pem \
--peer-cert-file ./certs/etcd-peer.pem \
--peer-key-file ./certs/etcd-peer-key.pem \
--peer-client-cert-auth \
--peer-trusted-ca-file ./certs/ca.pem \
--log-output stdout
```

节点3 `/opt/etcd/etcd-server-startup.sh`

```sh
#!/bin/sh
./etcd --name etcd-server03 \
--data-dir /data/etcd/etcd-server \
--listen-peer-urls https://172.31.253.123:2380 \
--listen-client-urls https://172.31.253.123:2379,http://127.0.0.1:2379 \
--quota-backend-bytes 8000000000 \
--initial-advertise-peer-urls https://47.52.170.97:2380 \
--advertise-client-urls https://47.52.170.97:2379,http://127.0.0.1:2379 \
--initial-cluster etcd-server01=https://47.106.123.217:2380,etcd-server02=https://47.75.196.191:2380,etcd-server03=https://47.52.170.97:2380 \
--ca-file ./certs/ca.pem \
--cert-file ./certs/etcd-peer.pem \
--key-file ./certs/etcd-peer-key.pem \
--client-cert-auth \
--trusted-ca-file ./certs/ca.pem \
--peer-ca-file ./certs/ca.pem \
--peer-cert-file ./certs/etcd-peer.pem \
--peer-key-file ./certs/etcd-peer-key.pem \
--peer-client-cert-auth \
--peer-trusted-ca-file ./certs/ca.pem \
--log-output stdout
```



`/etc/supervisord.d/etcd-server.ini`

```properties
[program:etcd-server01]
command=/opt/etcd/etcd-server-startup.sh                        ; the program (relative uses PATH, can take args)	
numprocs=1                                                      ; number of processes copies to start (def 1)
directory=/opt/etcd                                             ; directory to cwd to before exec (def no cwd)
autostart=true                                                  ; start at supervisord start (default: true)
autorestart=true                                                ; retstart at unexpected quit (default: true)
startsecs=30                                                    ; number of secs prog must stay running (def. 1)
startretries=3                                                  ; max # of serial start failures (default 3)
exitcodes=0,2                                                   ; 'expected' exit codes for process (default 0,2)
stopsignal=QUIT                                                 ; signal used to kill process (default TERM)
stopwaitsecs=10                                                 ; max num secs to wait b4 SIGKILL (default 10)
user=etcd                                                       ; setuid to this UNIX account to run the program
redirect_stderr=true                                            ; redirect proc stderr to stdout (default false)
stdout_logfile=/data/logs/etcd-server/etcd.stdout.log           ; stdout log path, NONE for none; default AUTO
stdout_logfile_maxbytes=64MB                                    ; max # logfile bytes b4 rotation (default 50MB)
stdout_logfile_backups=4                                        ; # of stdout logfile backups (default 10)
stdout_capture_maxbytes=1MB                                     ; number of bytes in 'capturemode' (default 0)
stdout_events_enabled=false                                     ; emit events on stdout writes (default false)
```

### 安装部署主控节点 apiserver

```sh
$ wget https://dl.k8s.io/v1.15.2/kubernetes-server-linux-amd64.tar.gz
$ tar xf kubernetes-server-linux-amd64.tar.gz -C /opt
$ cd /opt
$ mv kubernetes kubernetes-v1.15.2
$ ln -s /opt/kubernetes-v1.15.2/ /opt/kubernetes
$ cd /opt/kubernetes/server/bin
```

#### 签发 client 证书

`/opt/certs/client-csr.json`

```sh
$ cat <<EOF > /opt/certs/client-csr.json
{
    "CN": "k8s-node",
    "hosts": [
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
EOF
$ cd /opt/certs
$ cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=client client-csr.json | cfssljson -bare client
$ ls -l | grep client
-rw-r--r-- 1 root root  997 Dec 12 01:40 client.csr
-rw-r--r-- 1 root root  282 Dec 12 01:26 client-csr.json
-rw------- 1 root root 1675 Dec 12 01:40 client-key.pem
-rw-r--r-- 1 root root 1367 Dec 12 01:40 client.pem
```



```json
{
    "CN": "k8s-node",
    "hosts": [
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
```

#### 签发 server 证书

```sh
# 创建 kube-apiserver 服务端通讯证书配置文件
$ cat <<EOF > /opt/certs/apiserver-csr.json
{
    "CN": "k8s-apiserver",
    "hosts": [
      "127.0.0.1",
      "192.168.0.1",
      "kubernetes.default",
      "kubernetes.default.svc",
      "kubernetes.default.svc.cluster",
      "kubernetes.default.svc.cluster.local",
      "47.106.123.217",
      "47.75.196.191",
      "47.52.170.97"
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
EOF
$ cd /opt/certs
# 生成 server 通讯证书
$ cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=server apiserver-csr.json | cfssljson -bare apiserver

```

`/opt/certs/apiserver-csr.json`

```json
{
    "CN": "k8s-apiserver",
    "hosts": [
      "127.0.0.1",
      "192.168.0.1",
      "kubernetes.default",
      "kubernetes.default.svc",
      "kubernetes.default.svc.cluster",
      "kubernetes.default.svc.cluster.local",
      "47.106.123.217",
      "47.75.196.191",
      "47.52.170.97"
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
```

#### 分发证书、私钥至各从节点

```sh
$ cd /opt/certs
$ scp *.pem root@k8s-node1:/opt/certs
$ scp *.pem root@k8s-node2:/opt/certs
```

#### 配置并启动 kube-apiserver

##### 创建相关目录

```sh
$ mkdir /opt/kubernetes/server/bin/config
$ mkdir -p /data/logs/kubernetes/kube-apiserver
```

##### 编辑配置文件 

`/opt/kubernetes/server/bin/config/audit.yaml`

```sh
$ cat <<EOF > /opt/kubernetes/server/bin/config/audit.yaml
apiVersion: audit.k8s.io/v1beta1 # This is required.
kind: Policy
# Don't generate audit events for all requests in RequestReceived stage.
omitStages:
  - "RequestReceived"
rules:
  # Log pod changes at RequestResponse level
  - level: RequestResponse
    resources:
    - group: ""
      # Resource "pods" doesn't match requests to any subresource of pods,
      # which is consistent with the RBAC policy.
      resources: ["pods"]
  # Log "pods/log", "pods/status" at Metadata level
  - level: Metadata
    resources:
    - group: ""
      resources: ["pods/log", "pods/status"]

  # Don't log requests to a configmap called "controller-leader"
  - level: None
    resources:
    - group: ""
      resources: ["configmaps"]
      resourceNames: ["controller-leader"]

  # Don't log watch requests by the "system:kube-proxy" on endpoints or services
  - level: None
    users: ["system:kube-proxy"]
    verbs: ["watch"]
    resources:
    - group: "" # core API group
      resources: ["endpoints", "services"]

  # Don't log authenticated requests to certain non-resource URL paths.
  - level: None
    userGroups: ["system:authenticated"]
    nonResourceURLs:
    - "/api*" # Wildcard matching.
    - "/version"

  # Log the request body of configmap changes in kube-system.
  - level: Request
    resources:
    - group: "" # core API group
      resources: ["configmaps"]
    # This rule only applies to resources in the "kube-system" namespace.
    # The empty string "" can be used to select non-namespaced resources.
    namespaces: ["kube-system"]

  # Log configmap and secret changes in all other namespaces at the Metadata level.
  - level: Metadata
    resources:
    - group: "" # core API group
      resources: ["secrets", "configmaps"]

  # Log all other resources in core and extensions at the Request level.
  - level: Request
    resources:
    - group: "" # core API group
    - group: "extensions" # Version of group should NOT be included.

  # A catch-all rule to log all other requests at the Metadata level.
  - level: Metadata
    # Long-running requests like watches that fall under this rule will not
    # generate an audit event in RequestReceived.
    omitStages:
      - "RequestReceived"
EOF
```

##### kube-apiserver 启动脚本编写 

`/opt/kubernetes/server/bin/kube-apiserver.sh`

```sh
$ cat <<EOF > /opt/kubernetes/server/bin/kube-apiserver.sh
#!/bin/bash
./kube-apiserver \
  --apiserver-count 2 \
  --audit-log-path /data/logs/kubernetes/kube-apiserver/audit-log \
  --audit-policy-file /opt/kubernetes/server/bin/config/audit.yaml \
  --authorization-mode RBAC \
  --client-ca-file /opt/certs/ca.pem \
  --requestheader-client-ca-file /opt/certs/ca.pem \
  --enable-admission-plugins NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,DefaultTolerationSeconds,MutatingAdmissionWebhook,ValidatingAdmissionWebhook,ResourceQuota \
  --etcd-cafile /opt/certs/ca.pem \
  --etcd-certfile /opt/certs/client.pem \
  --etcd-keyfile /opt/certs/client-key.pem \
  --etcd-servers https://47.106.123.217:2379,https://47.75.196.191:2379,https://47.52.170.97:2379 \
  --service-account-issuer O \
  --service-account-signing-key-file /opt/certs/ca-key.pem \
  --service-account-key-file /opt/certs/ca-key.pem \
  --service-cluster-ip-range 10.254.0.0/16 \
  --service-node-port-range 3000-29999 \
  --target-ram-mb=1024 \
  --kubelet-client-certificate /opt/certs/client.pem \
  --kubelet-client-key /opt/certs/client-key.pem \
  --log-dir  /data/logs/kubernetes/kube-apiserver \
  --tls-cert-file /opt/certs/apiserver.pem \
  --tls-private-key-file /opt/certs/apiserver-key.pem \
  --v 2
EOF
```

创建 kube-apiserver 对应 supervisor 启动文件

```sh
$ cat <<EOF > /etc/supervisord.d/kub-apiserver.ini
[program:kube-apiserver-master]					
command=/opt/kubernetes/server/bin/kube-apiserver.sh            ; the program (relative uses PATH, can take args)
numprocs=1                                                      ; number of processes copies to start (def 1)
directory=/opt/kubernetes/server/bin                            ; directory to cwd to before exec (def no cwd)
autostart=true                                                  ; start at supervisord start (default: true)
autorestart=true                                                ; retstart at unexpected quit (default: true)
startsecs=30                                                    ; number of secs prog must stay running (def. 1)
startretries=3                                                  ; max # of serial start failures (default 3)
exitcodes=0,2                                                   ; 'expected' exit codes for process (default 0,2)
stopsignal=QUIT                                                 ; signal used to kill process (default TERM)
stopwaitsecs=10                                                 ; max num secs to wait b4 SIGKILL (default 10)
user=root                                                       ; setuid to this UNIX account to run the program
redirect_stderr=true                                            ; redirect proc stderr to stdout (default false)
stdout_logfile=/data/logs/kubernetes/kube-apiserver/apiserver.stdout.log        ; stderr log path, NONE for none; default AUTO
stdout_logfile_maxbytes=64MB                                    ; max # logfile bytes b4 rotation (default 50MB)
stdout_logfile_backups=4                                        ; # of stdout logfile backups (default 10)
stdout_capture_maxbytes=1MB                                     ; number of bytes in 'capturemode' (default 0)
stdout_events_enabled=false                                     ; emit events on stdout writes (default false)
EOF
```

##### 启动 kube-apiserver

```sh
$ chmod +x /opt/kubernetes/server/bin/kube-apiserver.sh
$ supervisorctl update 
$ netstat -luntp|grep kube-api
```



### 安装部署 kube-controller-manager

##### 创建相关目录

```sh
$ mkdir /data/logs/kubernetes/kube-controller-manager
```

##### 创建 kube-controller-manager 启动脚本

```sh
$ cat <<EOF > /opt/kubernetes/server/bin/kube-controller-manager.sh
#!/bin/sh
./kube-controller-manager \
  --cluster-cidr 172.7.0.0/16 \
  --leader-elect true \
  --log-dir /data/logs/kubernetes/kube-controller-manager \
  --master http://127.0.0.1:8080 \
  --service-account-private-key-file /opt/certs/ca-key.pem \
  --service-cluster-ip-range 192.168.0.0/16 \
  --root-ca-file /opt/certs/ca.pem \
  --v 2
EOF
```

##### 创建  kube-controller-manager 对应 supervisor 启动文件

```sh
$ cat <<EOF > /etc/supervisord.d/kube-controller-manager.ini
[program:kube-controller-manager-k8s-master]
command=/opt/kubernetes/server/bin/kube-controller-manager.sh                     ; the program (relative uses PATH, can take args)
numprocs=1                                                                        ; number of processes copies to start (def 1)
directory=/opt/kubernetes/server/bin                                              ; directory to cwd to before exec (def no cwd)
autostart=true                                                                    ; start at supervisord start (default: true)
autorestart=true                                                                  ; retstart at unexpected quit (default: true)
startsecs=30                                                                      ; number of secs prog must stay running (def. 1)
startretries=3                                                                    ; max # of serial start failures (default 3)
exitcodes=0,2                                                                     ; 'expected' exit codes for process (default 0,2)
stopsignal=QUIT                                                                   ; signal used to kill process (default TERM)
stopwaitsecs=10                                                                   ; max num secs to wait b4 SIGKILL (default 10)
user=root                                                                         ; setuid to this UNIX account to run the program
redirect_stderr=true                                                              ; redirect proc stderr to stdout (default false)
stdout_logfile=/data/logs/kubernetes/kube-controller-manager/controller.stdout.log  ; stderr log path, NONE for none; default AUTO
stdout_logfile_maxbytes=64MB                                                      ; max # logfile bytes b4 rotation (default 50MB)
stdout_logfile_backups=4                                                          ; # of stdout logfile backups (default 10)
stdout_capture_maxbytes=1MB                                                       ; number of bytes in 'capturemode' (default 0)
stdout_events_enabled=false                                                       ; emit events on stdout writes (default false)
EOF
```

##### 启动 kube-controller-manager

```sh
$ chmod +x /opt/kubernetes/server/bin/kube-controller-manager.sh
$ supervisorctl update 
$ supervisorctl status
$ netstat -luntp|grep kube-api
```

### 安装部署 kube-scheduler

##### kube-scheduler 编写启动脚本

```sh
$ cat <<EOF > /opt/kubernetes/server/bin/kube-scheduler.sh
#!/bin/sh
./kube-scheduler \
  --leader-elect  \
  --log-dir /data/logs/kubernetes/kube-scheduler \
  --master http://127.0.0.1:8080 \
  --v 2
EOF
```

##### 创建  kube-scheduler 对应 supervisor 启动文件

```sh
$ cat <<EOF > /etc/supervisord.d/kube-scheduler.ini
[program:kube-scheduler-k8s-master]
command=/opt/kubernetes/server/bin/kube-scheduler.sh                     ; the program (relative uses PATH, can take args)
numprocs=1                                                               ; number of processes copies to start (def 1)
directory=/opt/kubernetes/server/bin                                     ; directory to cwd to before exec (def no cwd)
autostart=true                                                           ; start at supervisord start (default: true)
autorestart=true                                                         ; retstart at unexpected quit (default: true)
startsecs=30                                                             ; number of secs prog must stay running (def. 1)
startretries=3                                                           ; max # of serial start failures (default 3)
exitcodes=0,2                                                            ; 'expected' exit codes for process (default 0,2)
stopsignal=QUIT                                                          ; signal used to kill process (default TERM)
stopwaitsecs=10                                                          ; max num secs to wait b4 SIGKILL (default 10)
user=root                                                                ; setuid to this UNIX account to run the program
redirect_stderr=true                                                     ; redirect proc stderr to stdout (default false)
stdout_logfile=/data/logs/kubernetes/kube-scheduler/scheduler.stdout.log ; stderr log path, NONE for none; default AUTO
stdout_logfile_maxbytes=64MB                                             ; max # logfile bytes b4 rotation (default 50MB)
stdout_logfile_backups=4                                                 ; # of stdout logfile backups (default 10)
stdout_capture_maxbytes=1MB                                              ; number of bytes in 'capturemode' (default 0)
stdout_events_enabled=false                                              ; emit events on stdout writes (default false)
EOF
```

##### 启动 kube-scheduler

```sh
$ chmod +x /opt/kubernetes/server/bin/kube-scheduler.sh
$ mkdir -p /data/logs/kubernetes/kube-scheduler
$ supervisorctl update
$ supervisorctl status
```

### kubelet

##### 创建生成证书签名请求 csr 的 json 配置文件

`/opt/certs/kubelet-csr.json`

```json
{
    "CN": "k8s-kubelet",
    "hosts": [
    "127.0.0.1",
    "47.75.196.191",
    "47.52.170.97"
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
```

##### 使用 kubelet-csr.json 配置文件生成证书

```sh
$ cd /opt/certs
$ cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=server kubelet-csr.json | cfssljson -bare kubelet
$ ls -l|grep kubelet
-rw-r--r-- 1 root root 1062 Jan 13 16:07 kubelet.csr
-rw-r--r-- 1 root root  342 Jan 13 15:59 kubelet-csr.json
-rw------- 1 root root 1679 Jan 13 16:07 kubelet-key.pem
-rw-r--r-- 1 root root 1415 Jan 13 16:07 kubelet.pem
```

##### 拷贝证书、私钥至所有主机

```sh
$ scp /opt/certs/kubelet.pem k8s-node1:/opt/certs/
$ scp /opt/certs/kubelet-key.pem k8s-node1:/opt/certs/
$ scp /opt/certs/kubelet.pem k8s-node2:/opt/certs/
$ scp /opt/certs/kubelet-key.pem k8s-node2:/opt/certs/

```

##### 选取一台 node 节点执行 set-cluster、set-credentials、set-context 、use-context

```sh
$ mkdir /opt/kubernetes/server/conf
$ cd /opt/kubernetes/server/conf
# set-cluster
$ kubectl config set-cluster k8s \
    --certificate-authority=/opt/certs/ca.pem \
    --embed-certs=true \
    --server=https://47.106.123.217:6443 \
    --kubeconfig=kubelet.kubeconfig
# set-credentials
$ kubectl config set-credentials k8s-node \
  --client-certificate=/opt/certs/client.pem \
  --client-key=/opt/certs/client-key.pem \
  --embed-certs=true \
  --kubeconfig=kubelet.kubeconfig 
# set-context 
$ kubectl config set-context k8s-context \
  --cluster=k8s \
  --user=k8s-node \
  --kubeconfig=kubelet.kubeconfig
# use-context
$ kubectl config use-context k8s-context --kubeconfig=kubelet.kubeconfig
# 分发至其他 node 节点
$ scp /opt/kubernetes/server/conf/kubelet.kubeconfig k8s-node2:/opt/kubernetes/server/conf/
```

##### 配置 k8s-node.yaml

`/opt/kubernetes/server/conf/k8s-node.yaml`

授予权限，角色绑定

只创建一次就好，存到etcd里,然后拷贝到各个node节点上

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: k8s-node
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:node
subjects:
- apiGroup: rbac.authorization.k8s.io
  kind: User
  name: k8s-node
```

K8s-master 执行

```sh
# 创建 k8s-node.yaml 文件
$ cat <<EOF > /opt/kubernetes/server/conf/k8s-node.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: k8s-node
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:node
subjects:
- apiGroup: rbac.authorization.k8s.io
  kind: User
  name: k8s-node
EOF
# 使用 k8s-node.yaml 文件
$ kubectl create -f /opt/kubernetes/server/conf/k8s-node.yaml

$ kubectl get clusterrolebinding k8s-node
NAME       AGE
k8s-node   2m12s
$ kubectl get clusterrolebinding k8s-node -o yaml
```

##### 准备 pause 基础镜像

pause镜像是k8s里必不可少的以pod方式运行业务容器时的一个基础容器。

```sh
$ docker pull kubernetes/pause
```

##### 创建 kubelet 启动脚本

K8s-node1

```sh
# --feature-gates="CSIMigration=false" 禁用新版特性  
$ cat <<EOF > /opt/kubernetes/server/bin/kubelet.sh
#!/bin/sh
./kubelet --anonymous-auth=false \
  --cgroup-driver systemd \
  --cluster-domain cluster.local \
  --runtime-cgroups=/systemd/system.slice \
  --kubelet-cgroups=/systemd/system.slice \
  --fail-swap-on="false" \
  --client-ca-file /opt/certs/ca.pem \
  --tls-cert-file /opt/certs/kubelet.pem \
  --tls-private-key-file /opt/certs/kubelet-key.pem \
  --hostname-override k8s-node1 \
  --image-gc-high-threshold 20 \
  --image-gc-low-threshold 10 \
  --kubeconfig /opt/kubernetes/server/conf/kubelet.kubeconfig \
  --log-dir /data/logs/kubernetes/kube-kubelet \
  --pod-infra-container-image kubernetes/pause \
  --root-dir /data/kubelet \
  --feature-gates="CSIMigration=false"
EOF


$ mkdir -p /data/logs/kubernetes/kube-kubelet /data/kubelet

$ chmod +x /opt/kubernetes/server/bin/kubelet.sh

# 创建 kubelet 启动文件
$ cat <<EOF > /etc/supervisord.d/kube-kubelet.ini
[program:kube-kubelet-k8s-node1]
command=/opt/kubernetes/server/bin/kubelet.sh     ; the program (relative uses PATH, can take args)
numprocs=1                                        ; number of processes copies to start (def 1)
directory=/opt/kubernetes/server/bin              ; directory to cwd to before exec (def no cwd)
autostart=true                                    ; start at supervisord start (default: true)
autorestart=true                                  ; retstart at unexpected quit (default: true)
startsecs=30                                      ; number of secs prog must stay running (def. 1)
startretries=3                                    ; max # of serial start failures (default 3)
exitcodes=0,2                                     ; 'expected' exit codes for process (default 0,2)
stopsignal=QUIT                                   ; signal used to kill process (default TERM)
stopwaitsecs=10                                   ; max num secs to wait b4 SIGKILL (default 10)
user=root                                         ; setuid to this UNIX account to run the program
redirect_stderr=true                              ; redirect proc stderr to stdout (default false)
stdout_logfile=/data/logs/kubernetes/kube-kubelet/kubelet.stdout.log   ; stderr log path, NONE for none; default AUTO
stdout_logfile_maxbytes=64MB                      ; max # logfile bytes b4 rotation (default 50MB)
stdout_logfile_backups=4                          ; # of stdout logfile backups (default 10)
stdout_capture_maxbytes=1MB                       ; number of bytes in 'capturemode' (default 0)
stdout_events_enabled=false                       ; emit events on stdout writes (default false)
killasgroup=true
stopasgroup=true
EOF

$ supervisorctl update

$ supervisorctl status

```

k8s-node2

```sh
$ cat <<EOF > /opt/kubernetes/server/bin/kubelet.sh
#!/bin/sh
./kubelet --anonymous-auth=false \
  --cgroup-driver systemd \
  --cluster-domain cluster.local \
  --runtime-cgroups=/systemd/system.slice \
  --kubelet-cgroups=/systemd/system.slice \
  --fail-swap-on="false" \
  --client-ca-file /opt/certs/ca.pem \
  --tls-cert-file /opt/certs/kubelet.pem \
  --tls-private-key-file /opt/certs/kubelet-key.pem \
  --hostname-override k8s-node2 \
  --image-gc-high-threshold 20 \
  --image-gc-low-threshold 10 \
  --kubeconfig /opt/kubernetes/server/conf/kubelet.kubeconfig \
  --log-dir /data/logs/kubernetes/kube-kubelet \
  --pod-infra-container-image kubernetes/pause \
  --root-dir /data/kubelet \
  --feature-gates="CSIMigration=false"
EOF

$ mkdir -p /data/logs/kubernetes/kube-kubelet /data/kubelet

$ chmod +x /opt/kubernetes/server/bin/kubelet.sh

# 创建 kubelet 启动文件
$ cat <<EOF > /etc/supervisord.d/kube-kubelet.ini
[program:kube-kubelet-k8s-node2]
command=/opt/kubernetes/server/bin/kubelet.sh     ; the program (relative uses PATH, can take args)
numprocs=1                                        ; number of processes copies to start (def 1)
directory=/opt/kubernetes/server/bin              ; directory to cwd to before exec (def no cwd)
autostart=true                                    ; start at supervisord start (default: true)
autorestart=true                                  ; retstart at unexpected quit (default: true)
startsecs=30                                      ; number of secs prog must stay running (def. 1)
startretries=3                                    ; max # of serial start failures (default 3)
exitcodes=0,2                                     ; 'expected' exit codes for process (default 0,2)
stopsignal=QUIT                                   ; signal used to kill process (default TERM)
stopwaitsecs=10                                   ; max num secs to wait b4 SIGKILL (default 10)
user=root                                         ; setuid to this UNIX account to run the program
redirect_stderr=true                              ; redirect proc stderr to stdout (default false)
stdout_logfile=/data/logs/kubernetes/kube-kubelet/kubelet.stdout.log   ; stderr log path, NONE for none; default AUTO
stdout_logfile_maxbytes=64MB                      ; max # logfile bytes b4 rotation (default 50MB)
stdout_logfile_backups=4                          ; # of stdout logfile backups (default 10)
stdout_capture_maxbytes=1MB                       ; number of bytes in 'capturemode' (default 0)
stdout_events_enabled=false                       ; emit events on stdout writes (default false)
killasgroup=true
stopasgroup=true
EOF

$ supervisorctl update

$ supervisorctl status
```

K8s-master

```sh
$ kubectl get nodes
NAME                      STATUS     ROLES    AGE     VERSION
k8s-node1                 Ready      <none>   60m     v1.20.0
k8s-node2                 Ready      <none>   6m15s   v1.20.0

# 给 nodes 节点分配 master、node 标签
$ kubectl label node k8s-node1 node-role.kubernetes.io/node=
node/k8s-node1 labeled
$ kubectl label node k8s-node1 node-role.kubernetes.io/master=
node/k8s-node1 labeled
$ kubectl label node k8s-node2 node-role.kubernetes.io/node=
node/k8s-node1 labeled
$ kubectl label node k8s-node2 node-role.kubernetes.io/master=
node/k8s-node1 labeled

$ kubectl get nodes
NAME        STATUS   ROLES         AGE   VERSION
k8s-node1   Ready    master,node   67m   v1.20.0
k8s-node2   Ready    master,node   13m   v1.20.0
```



### 部署 kube-proxy

##### 签发证书

K8s-master

`/opt/certs/kube-proxy-csr.json`

```json
{
    "CN": "system:kube-proxy",
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
```

```sh
$ cat <<EOF > /opt/certs/kube-proxy-csr.json
{
    "CN": "system:kube-proxy",
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "ST": "beijing",
            "L": "beijing",
            "O": "k8s",
            "OU": "cncf"
        }
    ]
}
EOF
# 生成证书和私钥
$ cd /opt/certs

$ cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=client kube-proxy-csr.json |cfssljson -bare kube-proxy-client

# 检查证书和私钥
$ ll  kube-proxy*
-rw-r--r-- 1 root root 1009 Jan 24 13:29 kube-proxy-client.csr
-rw------- 1 root root 1679 Jan 24 13:29 kube-proxy-client-key.pem
-rw-r--r-- 1 root root 1379 Jan 24 13:29 kube-proxy-client.pem
-rw-r--r-- 1 root root  269 Jan 24 13:06 kube-proxy-csr.json

# 拷贝至 k8s-node1、k8s-node2
$ scp /opt/certs/kube-proxy-client-key.pem k8s-node1:/opt/certs/
$ scp /opt/certs/kube-proxy-client.pem k8s-node1:/opt/certs/
$ scp /opt/certs/kube-proxy-client-key.pem k8s-node2:/opt/certs/
$ scp /opt/certs/kube-proxy-client.pem k8s-node2:/opt/certs/
```

##### 创建 kube-proxy 配置

k8s-node1

```sh
# 进入指定目录
$ cd /opt/kubernetes/server/bin/conf/

$ kubectl config set-cluster myk8s \
  --certificate-authority=/opt/certs/ca.pem \
  --embed-certs=true \
  --server=https://47.106.123.217:6443 \
  --kubeconfig=kube-proxy.kubeconfig
  
$ kubectl config set-credentials kube-proxy \
  --client-certificate=/opt/certs/kube-proxy-client.pem \
  --client-key=/opt/certs/kube-proxy-client-key.pem \
  --embed-certs=true \
  --kubeconfig=kube-proxy.kubeconfig
 
$ kubectl config set-context k8s-context \
  --cluster=k8s \
  --user=kube-proxy \
  --kubeconfig=kube-proxy.kubeconfig

$ kubectl config use-context k8s-context --kubeconfig=kube-proxy.kubeconfig
```





### k8s 指令摘要

```sh
$ ln -s /opt/kubernetes/server/bin/kubectl /usr/bin/kubectl
# 检查集群健康状态
$ kubectl get cs
# 获取 kubectl 节点
$ kubectl get nodes
NAME                      STATUS     ROLES    AGE     VERSION
izj6c3xhlwlv9s2r2p1o16z   NotReady   <none>   10h     v1.20.0
k8s-node1                 Ready      <none>   60m     v1.20.0
k8s-node2                 Ready      <none>   6m15s   v1.20.0
$ kubectl delete node izj6c3xhlwlv9s2r2p1o16z
node "izj6c3xhlwlv9s2r2p1o16z" deleted
```




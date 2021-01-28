```sh
# >/dev/null 2>&1 将标准输出流指向一个空地址
$ nohup  java -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1024m -Xms256m -Xmx6144m -Xmn512m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar app.jar >/dev/null 2>&1 &
# 将本地文件拷贝至远程服务器
scp kubernetes-server-linux-amd64.tar.gz root@k8s-master:~/k8s/apiserver/

$ ln –snf  /opt/kubernetes-v1.15.2 /opt/kubernetes
```

### ssh 免密登录 

- 各服务器生成密钥对

- 将公钥写入授权免密登录服务器 `~/.ssh/authorized_keys` 

```sh
$ cd ~/.ssh
# 生成密钥对
$ ssh-keygen
# 如果没有 authorized_keys 文件先创建一个
$ mkdir authorized_keys
$ cat id_rsa.pub >> authorized_keys
# 将公钥追加到远程服务器 授权远程服务器免密登录
$ cat ~/.ssh/id_rsa.pub | ssh root@47.106.123.217 "cat - >> ~/.ssh/authorized_keys"
$ cat ~/.ssh/id_rsa.pub | ssh root@47.75.196.191 "cat - >> ~/.ssh/authorized_keys"
$ cat ~/.ssh/id_rsa.pub | ssh root@47.52.170.97 "cat - >> ~/.ssh/authorized_keys"
```

### git

```sh
# 将当前分支与 master 分支合并
$ git merge master --allow-unrelated-histories
# 将误提交的文件从暂存区给删除掉,让git不再追踪这些文件
$ git rm -r --cache .idea/
# 将暂存区改动提交到本地版本库
$ git commit -m '删除误提交的文件' 
```


# Platform as a Service

1. O/S

   + Linux
     + RHEL及其衍生版本

       Fedora ---> CentOS Stream ---> RHEL ---> ~~CentOS~~, Oracle Linux, AlmaLinux, Rocky Linux, _Red Flag Linux_
     + Debian及其衍生版本
     
       Debian ---> _deepin_, Kali, Knoppix, Ubuntu ---> _Linux Mint_, _elementary OS_
     + SUSE
     + Slackware
     + ~~Mandriva~~
     + Gentoo
     + Arch
     + Puppy Linux
     + Alpine Linux
   + Windows Server
   + Unix
     + BSD

       ~~Sun OS~~, FreeBSD, NetBSD ---> OpenBSD
     + Solaris & ~~OpenSolaris~~
     + AIX
     + HP-UX
     + SCO UnixWare
   
   |            | Windows Server                                         | RHEL/CentOS                      | Debian/Ubuntu                                | FreeBSD                                              |
   |:-----------|:-------------------------------------------------------|:---------------------------------|:---------------------------------------------|:-----------------------------------------------------|
   | CPU        | amd64                                                  | x86, amd64, arm, arm64, ppc      | x86, amd64, arm, arm64, mips, ppc, risc-v, z | x86, amd64, arm, arm64, ppc, risc-v                  |
   | POSIX兼容    | -                                                      | Y                                | Y                                            | [Y*](https://wiki.freebsd.org/FreeBSD_and_Standards) |
   | 进程管理: 通信   | Windows Socket, pipe, shared memory, DDE, ~~mailslot~~ | unix socket, pipe, shared memory | unix socket, pipe, shared memory             | BSD socket, pipe, shared memory                      |
   | 进程管理: 调度   |                                                        |                                  |                                              |                                                      |
   | 存储管理       |                                                        | swap, mmap                       |                                              |                                                      |
   | 设备管理       |                                                        | APIC, DMA, SPOOLING              |                                              |                                                      |
   | 文件管理: 逻辑卷  | LV                                                     | LVM                              | LVM                                          | LVM(ZFS)                                             |
   | 文件管理: 文件系统 | FAT32, NTFS; SMB                                       | XFS, EXT4; NFS, SMB; GFS2        | EXT4, JFS, XFS, Btrfs; NFS; GFS2             | UFS, ZFS; NFS                                        |
   | 文件管理: RAID | RAID 0, 5                                              | RAID 0, 1, 4, 5, 6, 10           | RAID 5, 10                                   | RAID 0, 1, 3                                         |
   | 作业管理       |                                                        |                                  |                                              |                                                      |
   | 人机交互       | GUI/cmd/PowerShell                                     | GUI/shell(bash)                  | GUI/shell(bash)                              | GUI/shell(bash)                                      |
   | 包管理        | -                                                      | yum                              | apt                                          | pkg                                                  |
   
   Windows的带区卷使用多个磁盘，读写速度更快；跨区卷读写速度慢，但可以利用多个磁盘的剩余空间组合成更大的容量。

   __注：amd64也称之为x86_64，arm64也称之为AArch64__

2. DBMS

    + RDBMS
        + [PostgreSQL](https://www.postgresql.org/)
        + [MySQL](https://www.mysql.com/)
        + Oracle
        + DB2
        + SQL Server
    + NoSQL
        + [MongoDB](https://www.mongodb.com/)
        + [Apache Cassandra](https://cassandra.apache.org/_/index.html)
        + Redis
          
          [Dragonfly](https://www.dragonflydb.io/) 是一款开源的、兼容redis和memcache的高性能内存数据库。
      
          [Garnet](https://github.com/microsoft/garnet) 是微软开源的采用MIT协议、兼容redis的高性能远端缓存服务。
      
          [SSDB](https://github.com/ideawu/ssdb) 是一款采用BSD协议，基于Google LevelDB，兼容redis的NoSQL数据库。
          
        + [ElasticSearch](https://www.elastic.co/)
        + [Neo4j](https://neo4j.com/)
    + OLAP
      + [ClickHouse](https://clickhouse.com/)
      + [Apache Doris](https://doris.apache.org/)
      + [Apache Flink](https://flink.apache.org/)
      + [Apache Impala](https://impala.apache.org/)
      + [Apache Kylin](https://kylin.apache.org/)

3. Middleware

   + HTTP服务器
     + [Nginx](https://nginx.org)
     + [Apache HTTPd](https://httpd.apache.org)
     + IIS
     + [Caddy](https://caddyserver.com)
     + [Pingora](https://github.com/cloudflare/pingora)
   + HTTP缓存代理服务器
     + [Varnish](https://varnish-cache.org)
     + [Squid](https://www.squid-cache.org)
   + 消息服务器
     + AMQP: RabbitMQ
     + JMS: -
     + [Apache RocketMQ](http://rocktmq.apache.org/)
     + [Apache Kafka](https://kafka.apache.org/)
     + [Apache Pulsar](https://pulsar.apache.org/)
     + MQTT: -
   + Web Server(Servlet container)
     + [Apache Tomcat](https://tomcat.apache.org/)
     + [Eclipse Jetty](https://jetty.org/)
     + [Undertow](https://undertow.io/)
   + Application Server(EJB container)
     + [Wildfly](https://www.wildfly.org/)
     + [Apache Tomee]((https://tomcat.apache.org/))
     + [Eclipse GlassFish](https://glassfish.org/)
     + WebLogic
     + WebSphere
   + 分布式协调

     |                      | zookeeper                                                         | consul                                      | etcd                                                   |
     |:---------------------|:------------------------------------------------------------------|:--------------------------------------------|:-------------------------------------------------------|
     | License              | ASL                                                               | MPL?                                        | ASL                                                    |
     | company/organization | Apache                                                            | HashiCorp                                   | CNCF                                                   |
     | language             | java                                                              | go                                          | go                                                     |
     | consensus algorithm  | zab(paxos)                                                        | raft                                        | raft                                                   |
     | protocol             | TCP                                                               | HTTP                                        | gRPC                                                   |
     | storage              | file                                                              | MemDB(data), BoltDB(raft logs)              | file, BoltDB(raft logs)                                |
     | role                 | leader, follower, watcher                                         | leader, follower                            | leader, follower                                       |
     | usage                | naming, configuration, distributed synchronization, group service | service discovery, configuration management | service discovery, kv store, pub/sub, distributed lock |
     | performance          |                                                                   |                                             | 10000 writes/s                                         |

   + 微服务治理平台
     + API网关

       |                        | zuul        | spring-cloud-gateway                     | APISIX                                        | ShenYu                                                 |
       |:-----------------------|:------------|:-----------------------------------------|:----------------------------------------------|:-------------------------------------------------------|
       | License                | ASL         | ASL                                      | ASL                                           | ASL                                                    |
       | language               | Java/Groovy | Java                                     | Lua, plugin: Java, Go, Python                 | Java                                                   |
       | web server             | jetty       | reactor-netty-http                       | nginx                                         | reactor-netty-http                                     |
       | service discovery      | eureka      | eureka, redis, properties                | etcd, consul, zk, NaCos, eureka               | HTTP, WebSocket, zk, etcd, NaCos                       |
       | proxy                  | HTTP        | HTTP, WebSocket, gRPC                    | HTTP                                          | HTTP, WebSocket, gRPC, MQTT, Dubbo/SOFA                |
       | load balance           | ribbon      | spring-cloud-loadbalancer/ribbon         | round robin                                   | random, round robin, ip hash                           |
       | rate limiter           | __NA__      | redis, token bucket                      | limit-req, limit-conn, limit-count            | redis: token bucket, leaky bucket, sliding time window |
       | circuit breaker        | hystrix     | spring-cloud-circuitbreaker-resilience4j | api-breaker                                   | Hystrix/Resilience4j/Sentinel                          |
       | authentication         | __NA__      | OAuth2                                   | OAuth2, JWT, basic, OIDC, CAS, LDAP           | OAuth2, JWT, encrypt/decrypt                           |
       | security               | __NA__      | -                                        | ip, ua, referer, csrf, waf                    | waf, encrypt/decrypt                                   |
       | observability: Tracers | servo       | -                                        | Zipkin/SkyWalking/otlp                        | __NA__                                                 |
       | observability: Metrics | servo       | MicroMeter                               | Prometheus/DataDog                            | Prometheus                                             |
       | observability: Loggers | -           | -                                        | TCP/UDP/HTTP/syslog/kafka/RocketMQ/loki       | kafka/RocketMQ/Pulsar                                  |
       | canary release         | __NA__      | __NA__                                   | traffic-split                                 | divide: a/b test, grayscale test                       |
       | other                  | -           | cache, retry                             | cache, request validation, proxy mirror, mock | mock: SpEL, cache, parameter mapping                   |
       | dashboard              | __NA__      | __NA__                                   | APISIX Dashboard                              | shenyu-admin                                           |
       | API management         | __NA__      | __NA__                                   | -                                             | pull from swagger, manual, client registration         |
       | performance            |             |                                          |                                               |                                                        |

       [Kong Gateway](https://konghq.com/products/kong-gateway) 是一个基于openresty的网关，它支持下游服务使用HTTP、gRPC协议，提供Kong Manager作为Web UI来配置路由关系及需要应用的处理插件。
       Kong Gateway Enterprise提供了企业级内置插件，如限流、认证、请求校验、结果缓存、mock等特性。

     + 注册中心 & 配置中心

       注册中心负责服务注册与发现，容许各节点数据短暂不一致（导致客户端数据未即时删除旧数据或获得新数据），但要求较高的可用性和分区性。

       |              | eureka        | NaCoS                   | Consul                              | Etcd               |
       |:-------------|---------------|:------------------------|:-------------------|:---------|
       | License      | ASL           | ASL                     | HashiCorp + Business Source License | ASL                |
       | language     | java          | java                    | Go                                  | Go                 |
       | CAP          | AP            | CP + AP                 | CA(Raft)                            | CP(Raft)           |
       | protocol     | HTTP          | HTTP                    | HTTP, DNS                           | HTTP, gRPC         |
       | data-sync    | peer replica  | UDP multicast/gRPC      | Gossip                              | leader-->followers |
       | lease        | Y             | Y                       | Y(session)                          | Y                  |
       | health-check | Y(heartbeat)  | Y                       | Y                                   | Y(TTL)             |
       | region-aware | remote-region | nacos-sync              | locality(Consul Enterprise)         |                    |
       | zone-aware   | Y             | NA                      | locality(Consul Enterprise)         |                    |
       | data model   | app/instance  | service/cluster/instance | service                             | KV                 |
       | auth         | NA            | authn, authz            | ACL                                 |                    |
       | performance |               |                         |                                     | 10000 write/sec    |
       
       配置中心负责统一管理应用配置，是应用获取配置的重要来源，也是开发、运维人员动态下发配置的管理控制服务。
       
       |                    | Apollo                       | NaCos                  | ~~Archaius~~        | ~~DisConf~~    |
       |:-------------------|:-----------------------------|:-----------------------|:--------------------|:---------------|
       | License            | Apache                       | Apache                 | Apache              | GPL            |
       | server language    | Java                         | Java                   | Java                | Java           |
       | sync               | long polling                 | long polling           | fixed delay polling | watch notify   |
       | storage            | MySQL                        | MySQL, Derby           | unknown             | MySQL          |
       | consistency        | database mq                  | async http             | unknown             | Zookeeper      |
       | dashboard          | apollo-portal                | console                | unknown             | disconf-web    |
       | gray publish       | Y                            | NA                     | unknown             | -              |
       | lock config        | NA                           | NA                     | unknown             | Y              |
       | share config       | Y                            | Y                      | unknown             | Y              |
       | audit              | Y                            | Y                      | unknown             | Y              |
       | AuthN & AuthZ      | Y                            | AuthN                  | unknown             | AuthN          |
       | version management | Y                            | Y                      | unknown             | Y              |
       | client             | Java, .NET, HTTP             | Java, HTTP             | Java                | Java, HTTP     |
       | change listener    | Y                            | Y                      | Y                   | Y              |
       | design             | app.id/env/cluster/namespace | namespace/group/dataId | -                   | app/env/config |
       | profile            | env                          | namespace              | @environment        | env            |
       | file               | namespace                    | dataId                 | -                   | config         |
       | region             | NA                           |                        | @region             | NA             |
       | zone               | idc/cluster                  |                        | @zone               | NA             |
       | performance        | r(9000), w(1100)             | r(15000), w(1800)      | unknown             | -              |      

     + 认证与鉴权
     
       |                            | [Apereo CAS](https://www.apereo.org/projects/cas)               | [Keycloak](https://www.keycloak.org)                         | [OpenAM](https://github.com/OpenIdentityPlatform/OpenAM) |
       |:---------------------------|:----------------------------------------------------------------|:-------------------------------------------------------------|:---------------------------------------------------------|
       | License                    | Apache                                                          | Apache                                                       | CDDL                                                     |
       | client                     | Java, .NET, PHP, Perl, etc.                                     | Java, JavaScript, C#, Python                                 | Java                                                     |
       | Authentication             | LDAP, Database, X.509, SPNEGO, JAAS, JWT, RADIUS, etc.          | LDAP, AD, Database, kerberos, WebAuthn                       | LDAP, AD, Database, kerberos, WebAuthn                   |
       | federation                 | LDAP                                                            | LDAP, AD, kerberos                                           | LDAP, AD, kerberos                                       |
       | Authorization              | ABAC                                                            | ABAC, RBAC, UBAC, CBAC, rule-based/time-based access control | JWT                                                      |
       | protocol                   | CAS, SAML, WS-Federation, OAuth2, OIDC, REST                    | SAML, OAuth2, OIDC                                           | SAML, OAuth2, OIDC, NTLM                                 |
       | multifactor authentication | one-time passwords, 2-step verification codes, email, sms, etc. | OTP                                                          | 2FA                                                      |
       | delegated authentication   | CAS servers, SAML IdP, OAuth2 IdP, OIDC IdP                     | SAML IdP, OAuth2 IdP, OIDC IdP                               | OAuth2 IdP                                               |
       | HA                         | clustering(cache node replication) --> re-authenticate          | clustering                                                   | clusterization(session failover)                         |
       | performance                |                                                                 |                                                              |                                                          |
       
       Apereo CAS 前身为Yale大学研发的 [CAS](https://developers.yale.edu/cas-central-authentication-service) ，2004年时成为Jasig 的一个项目（产品名称也改成了JASIG CAS），后Jasig基金会和Sakai基金会合并为Apereo基金会。
       
       Keycloak 是 CNCF 基金会开源项目，是RH-SSO(RedHat Single Sign-On) 上游项目。
       
       OpenAM 由ForgeRock公司发起，前身为 Sun Open SSO。
   
       [FusionDirectory](https://www.fusiondirectory.org) 是OW2组织下一款采用GPL协议开源的IAM软件。

4. Runtime

   编译型语言，如C、Go、Rust，需要有编译器将源代码编译成特定CPU架构和特定操作系统的本地代码，如果程序依赖库（Windows平台的dll，或Linux的so），
则还需要链接器，最终生成可执行文件（如Windows的PE文件，或Linux的ELF文件）。

   解释型语言，如Java，同样需要编译器（如javac），不过这里的编译器只是将源代码转换成字节码一类的中间代码，还需要VM这一本地应用程序，对中间码进行解释执行。
（由于JVM并不只是解释器，对于热点代码，JVM会将字节码编译成本地代码）

   |            | 可编译的语言                                | 构建脚本语言            | 默认构建脚本                                  | 子模块                 | 交叉编译 | 跨平台 | 依赖管理             | 发布        |
   |:-----------|:--------------------------------------|:------------------|:----------------------------------------|:--------------------|:-----|:----|:-----------------|:----------|
   | Make       | C                                     | DSL               | GNUmakefile or __Makefile__ or makefile | -                   | N    | N   | -                | -         |
   | CMake      | C/C++                                 | DSL               | CMakeLists.txt                          |                     | N    | Y   |                  | -         |
   | Bazel      | C/C++, Java, Kotlin, Python, Go, Rust | starlark          | WORKSPACE, BUILD, **/\*.bzl             | **/BUILD, **/\*.bzl | Y    | Y   | Y                | -         | 
   | ANT        | Java                                  | xml               | build.xml                               | build.xml           | -    | *Y* | Ivy              | Ivy       |
   | Maven      | Java                                  | xml               | pom.xml                                 | pom.xml             | -    | *Y* | Maven            | Y         |
   | Gradle     | Java                                  | groovy, kotlin    | settings.gradle, build.gradle           | build.gradle        | -    | *Y* | Y                | _Y_       |
   | setuptools | Python                                | python, ini       | setup.py or setup.cfg                   | -                   | N    | Y   | pip              | Y(.whl)   |
   | PyBuilder  | Python                                | python, toml      | setup.py, pyproject.toml                | Y?                  | N    | Y   | Y                | distutils |
   | Poetry     | Python                                | toml              | pyproject.toml                          | ?                   | N    | Y   | Y                | Y(.whl)   |
   | Hatch      | Python                                | toml              | pyproject.toml or hatch.toml            | ?                   | N    | Y   | venv or uv       | Y         |
   | PDM        | Python                                | toml              | pyproject.toml, pdm.toml                | pyproject.toml      | N    | Y   | venv or uv       | Y         |
   | Rake       | Ruby                                  | DSL like makefile | Rakefile                                | -                   | N    | Y   | gem              | bundler   |
   | go build   | Go                                    | -                 | -                                       | go.work             | Y    | N   | go.mod           | -         |
   | Cargo      | Rust                                  | toml              | Cargo.toml, **/.cargo/config.toml       | Cargo.toml          | Y    | N   | Cargo            | Cargo     |
   | -          | NodeJS                                | javascript        |                                         |                     | -    | -   | npm, pnpm, bower |           |

   *PyBuilder 是一个类似于Maven的Python构建工具。*

   *Groovy可以通过ANT、Gant、Gradle或Maven进行编译，使用Grape进行依赖管理。*

   *Clojure使用Leiningen管理、发布依赖。*

5. DevSecOps

   + 知识库
     + [xwiki](https://www.xwiki.org)
     + [MediaWiki](https://www.mediawiki.org/wiki/MediaWiki)
     + [DokuWiki](https://www.dokuwiki.org/dokuwiki)
     + Atlassian Confluence
   + 项目管理
     + [Redmine](https://www.redmine.org/)
     + Atlassian Jira
   + 代码仓库
     + [GitLab](https://gitlab.com/gitlab-org/gitlab)
     + [Gitea](https://about.gitea.com/products/gitea/)
     + [Gogs](https://gogs.io/)
   + 制品库
     + [Nexus Repository](https://www.sonatype.com/products/sonatype-nexus-oss-download)
     + [JFrog Artifactory](https://jfrog.com/community/download-artifactory-oss/)
   + CI
     + [Jenkins](https://www.jenkins.io/)
     + ~~Apache Continuum~~
     + JetBrains TeamCity
     + Atlassian Bamboo
   + security testing tools
     + **S**oftware **C**omposition **A**nalysis: 检查源代码及依赖的开源代码是否有（CVE、NVD）已知漏洞、检查容器镜像是否有安全问题，检查许可证是否有问题
       + [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
       + [OWASP Dependency-Track](https://dependencytrack.org/)
       + JFrog Xray
       + Black Duck
       + Contrast SCA
     + **S**tatic **A**pplication **S**ecurity **T**esting: 在SDLC的编码阶段分析应用程序的源代码或二进制代码文件的语法、结构、过程、接口来发现程序代码存在的安全漏洞
       + [SpotBugs](https://spotbugs.github.io/)
       + [SonarQube](https://www.sonarsource.com/products/sonarqube/)
       + HP Fortify
       + Synopsys Coverity
       + Contrast Scan
     + **D**ynamic **A**pplication **S**ecurity **T**esting: 一般在测试阶段模拟黑客工具，分析Web应用漏洞
       + [OWASP ZAP](https://www.zaproxy.org/)
       + IBM AppScan
       + Acunetix Web Vulnerability Scanner
     + **I**nteractive **A**pplication **S**ecurity **T**esting：通过代理、VPN或服务端部署agent，收集、监控web应用程序运行时函数执行、数据传输，并与扫描器端进行实时交互，高效准确的识别安全缺陷即漏洞，同时可以准确的确定漏洞所在的代码文件、行数、函数及参数
       + [OpenRASP](https://rasp.baidu.com/)
       + Synopsys Seeker
       + Contrast Assess & Contrast Protect
   + 镜像库
     + [registry](https://docs.docker.com/registry/)
     + [Harbor](https://goharbor.io/)
   + CD
     + [Spinnaker](https://spinnaker.io/)
     + [GoCD](https://www.gocd.org/)
     + [Argo CD](https://argo-cd.readthedocs.io/)
     + [Concourse](https://concourse-ci.org/)
     + [Screwdriver](https://screwdriver.cd/)
   + 监控告警
     + [ELK Stack](https://www.elastic.co/cn/elastic-stack/)
     + [Zabbix](https://www.zabbix.com/)
     + [Prometheus](https://prometheus.io/)
     + [Grafana](https://grafana.com/)
   + 远程桌面
     + [Guacamole](https://guacamole.apache.org/) 是一个无客户端的远程桌面网关，它支持RDP、VNC、SSH协议


6. 安全

   |               | 协议      | 工具         | 示例                                                                                                                                                                           | 解决办法                     | 备注                               |
   |:--------------|:--------|:-----------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------|:---------------------------------|
   | 端口扫描          | ICMP    | nmap       | nmap -v -sn 192.168.1.0/24                                                                                                                                                   | 禁止Ping                   | 局域网                              |
   | ping of death | ICMP    | ping       | ping -t -l 65500 1.2.3.4                                                                                                                                                     | 禁止Ping                   | DoS，报文服务端重组导致缓冲区溢出               |
   | Smurf         | ICMP    | -          | -                                                                                                                                                                            | 过滤广播地址，启用反向路经过滤          | DDoS，伪造源IP地址为目标网络的广播地址           |
   | 断网攻击          | ARP     | arpspoof   | arpspoof -i eth0 -t 192.168.1.3 192.168.1.1                                                                                                                                  | 绑定MAC与IP关系               | 局域网                              |
   | ARP欺骗         | ARP     | arpspoof   | echo 1 > /proc/sys/net/ipv4/ip_forward; arpspoof -i eth0 -t 192.168.1.3 -r 192.168.1.1                                                                                       | 绑定MAC与IP关系               | 局域网                              |
   | DNS劫持         | DNS     | dnsspoof   | echo 1 > /proc/sys/net/ipv4/ip_forward; arpspoof -i eth0 -t 192.168.1.3 -r 192.168.1.1; dnsspoof -i eth0 -f hosts.txt                                                        | DNSSEC/HTTPS-DNS，使用Hosts | 局域网，基于ARP欺骗，伪造DNS应答              |
   | DNS劫持         | DNS     | -          | -                                                                                                                                                                            | 使用可信DNS服务器               | 中间人攻击                            |
   | SYN-Flood     | TCP     | -          | -                                                                                                                                                                            | SYN-PROXY                | DoS，代理通过Syncookie，避免客户端直连消耗服务端资源 |
   | LandAttack    | TCP     | -          | -                                                                                                                                                                            |                          | DoS，服务器向自己发送SYN-ACK              |
   | UDP-Flood     | UDP     | -          | -                                                                                                                                                                            | 限流，指纹过滤                  | DDoS，源IP改为攻击目标IP                 |
   | teardrop      | UDP     | -          | -                                                                                                                                                                            | 防火墙，系统升级                 | 后继UDP包offset小于上一个UDP包长度          |
   | HTTP嗅探        | HTTP    | urlsnarf   | echo 1 > /proc/sys/net/ipv4/ip_forward; arpspoof -i eth0 -t 192.168.1.3 -r 192.168.1.1; urlsnarf -i eth0                                                                     | HTTPS                    | 局域网，基于ARP欺骗                      |
   | 窃听            | HTTPS   | sslstrip   | echo 1 > /proc/sys/net/ipv4/ip_forward; arpspoof -i eth0 -t 1.1.1.3 -r 1.1.1.1; iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080; sslstrip | HSTS                     | 局域网，基于ARP欺骗和HTTPS降级              |
   | HTTP劫持        | HTTP    | -          | mitmproxy -p 7890                                                                                                                                                            | HTTPS                    | 运营商在HTTPS全面推开前往ISP页面插入广告         |
   | 暴力破解          | -       | hydra      | hydra -L users.txt -P passwords.txt ftp://192.168.0.1                                                                                                                        | 复杂密码，账号锁定                |                                  |
   | XSS           | HTTP(S) | -          | https://owasp.org/www-community/attacks/xss/                                                                                                                                 | 特殊字符转义，WAF               | 浏览器执行脚本导致                        |
   | SQL注入         | SQL     | sqlmap     | export T="http://site"; sqlmap -u "${T}/q=1" --tables; sqlmap -u "${T}" --cookie "q=1" --level 2 --columns -T "table"; sqlmap -u "${T}/q=1" --dump -C "columns" -T "table"   | 预编译SQL，WAF               |                                  |
   | 病毒/木马         | -       | metasploit | msf; use {exploit}/{os}/{tools}/{bug}; show options; set ${key} ${value}; set PAYLOAD/{os}/{tools}/{kit}; show options; set ${key} ${value}; exploit                         | 软件升级至安全版本，验证软件签名         |                                  |
   | 爬虫            | HTTP(S) | scrapy     | -                                                                                                                                                                            | robots.txt，验证码，WAF       | webdriver                        |
   | CC            | HTTP(S) | -          | -                                                                                                                                                                            | 限速/禁止符合特征来源的访问，WAF       |                                  |
   | DDoS          | -       | -          | -                                                                                                                                                                            | 丢弃符合特征来源的数据包，防火墙，WAF     |                                  |
   | 水平越权          | -       | -          | -                                                                                                                                                                            | 资源所属用户与访问用户匹配检测          |                                  |
   | 垂直越权          | -       | -          | -                                                                                                                                                                            | 资源所属角色与用户角色匹配检测          |                                  |

   + Intrusion Prevention System
     + [Snort](https://www.snort.org) 是一款C语言开发的，开源、跨平台、实时，基于规则的网络入侵检测工具。
   + 防火墙
     + [pfSense](https://www.pfsense.org/) 从 m0n0wall 开源项目分叉而来，它是具有WebGUI管理界面的FreeBSD开源软件（更像是基于FreeBSD的操作系统）。支持配置入站/出站规则，提供VLAN、NAT、VPN(IPSec、PPTP、L2TP、OpenVPN、WireGuard)、SNMP、NTPD、DHCP、DNS、DDNS等能力，支持流量整形。
     + [OPNsense](https://opnsense.org/) 是从pfSense分叉而来，使用BSD许可证分发的开源软件。
     + [IPFire](https://www.ipfire.org/) 是具有WebGUI管理界面的Linux开源软件（更像是Linux发行版）。
   + Web Application Firewall
     + [ModSecurity](https://github.com/owasp-modsecurity/ModSecurity) 是一个采用Apache 2.0开源许可证授权的WAF软件。它最初由Trustwave研发，作为Apache httpd的模块分发是。目前也支持Nginx、IIS等HTTP服务器，可以使用OWASP组织维护的CRS规则集、Comodo维护的规则集、AtomiCorp维护的规则集。
     + [Coraza](https://coraza.io/) 是一个采用Apache 2.0协议，用Go语言移植ModSecurity项目，100%兼容OWASP核心规则集的开源、企业级应用防火墙动态库。
     + [雷池](https://waf-ce.chaitin.cn/) 是长亭科技开发的WAF，宣称与ModSecurity相比，其检出率高、误报率低、性能好。其架构较为复杂，分为网关（基于Nginx/tengine）、管理容器、检测容器、日志容器，还依赖关系型数据库。
     + [aihhtps](https://github.com/qq4108863/hihttps) 支持传统的ModSecurity正则规则，也支持无监督机器学习的开源软件。
   + 堡垒机（认证、授权、审计）
     + [JumServer](https://www.jumpserver.org/) 是一个符合4A标准（账号、认证、授权、审计），使用GPL许可证的开源软件，企业版提供功能增强和服务支持。
   + Vulnerability Scanner
     + [OpenVAS](https://openvas.org/) 是一款使用C和Rust语言编写，采用GPL协议开源的漏洞扫描工具。
   + KMS
     + [Conjur](https://www.conjur.org/)
     + [Vault](https://www.vaultproject.io/)
   + CA
     + [EJBCA](https://www.ejbca.org/)

   *[ARP](https://info.support.huawei.com/info-finder/encyclopedia/zh/ARP.html) 攻击利用了ARP广播请求（gateway ip, gateway mac, host ip），主机单播应答（host ip, host mac, gateway ip, gateway mac）机制*

---------------------------------------
## 操作系统

1. [Linux From Scratch](https://www.linuxfromscratch.org)

   ```shell
   # 内核裁剪
   
   # 系统软件、应用软件
   
   # 安全加固
   ```

2. 字体库

   |         | [思源](https://github.com/adobe-fonts) | [Unifont](https://www.unifoundry.com/unifont/index.html) | [文泉驿](http://wenq.org)             |
   |:--------|:-------------------------------------|:---------------------------------------------------------|:-----------------------------------|
   | License | OFL                                  | GPL & OFL                                                | GPL & Apache                       |
   | 字符集     | GB18030                              | -                                                        | GB18030                            |
   | 点阵字体    | -                                    | pcf, bdf                                                 | LiberationSans-12pt.bdf            |
   | 矢量字体    | otf<br>otc<br>ttc<br>woff2           | otf                                                      | wqy-zenhei.ttc<br>wqy-microhei.ttc |
   | 衬线      | 思源宋体                                 |                                                          |                                    |
   | 无衬线     | 思源黑体                                 |                                                          | wqy-zenhei.ttc                     |
   | 其他      |                                      | 包含Unicode BMP字符                                          | 微米黑仅支持到GBK，字体文件小，需授权               |

   *点阵字体放大会出现锯齿、模糊，主要格式有bdf、pcf、fnt、hbf。*

   *矢量字体利用直线和贝塞尔曲线来描述字体。每个字形中存储的是一系列控制点，由控制点产生字的轮廓，再进行填充颜色。*
   *矢量字体常用的格式有Adobe于1985年发明的PostScript Type 1，即pfa、pfb格式字体； Apple于1991年发布的TrueType字体，即ttf、ttc格式字体； Microsoft与Adobe合作研发的，扩展TrueType字体格式的TrueType Open字体（后名为OpenType字体），即otf、otc格式字体。*

   *目前网络上常用的Web Open Font Format采用压缩格式，更适合网页使用，其下一代标准WOFF2有更高的压缩率。*

   *衬线（Serif）字体在笔画的开始和结束地方有额外的装饰，而且笔画的粗细会有所不同。*

   *从中文来说，字符集主要考虑是否兼容GB2312、GBK、GB18030这些字符集。*

## HTTP服务器

   ```shell
   # nginx HA
   
   ```

## 数据库

1. [PostgreSQL HA](https://www.postgresql.org/docs/current/different-replication-solutions.html)

   |             | [patroni](https://github.com/zalando/patroni) | [PostgreSQL Automatic Failover](https://clusterlabs.github.io/PAF) | [repmgr](https://www.repmgr.org) |
   |:------------|:----------------------------------------------|:-------------------------------------------------------------------|:---------------------------------|
   | License     | MIT                                           | PostgreSQL                                                         | GPL                              |
   | maintenance | -                                             | -                                                                  | EDB                              |
   | method      | streaming replication                         | streaming replication                                              | stream replication               |
   | roles       |                                               | master, slave                                                      | master, slave, _witness_         |
   | switch over | Y                                             | Y                                                                  | Y                                |
   | fail over   | Y                                             | Y                                                                  | Y                                |
   | other       | Python, ZooKeeper/Consul/etcd                 | Perl/Ruby, Pacemaker & Corosync                                    | -                                |

   三种高可用方式另可参考[PostgreSQL-Compare-High-Availability](https://postgresql.scalegrid.io/hubfs/PostgreSQL-Compare-High-Availability-Frameworks-Infographic-ScaleGrid-DBaaS.pdf) 。

   _[PgBouncer](https://www.pgbouncer.org)伪装成数据库服务器，来提供数据库连接池能力。 
PgBouncer支持三种连接池模式：会话模式（客户端断开时归还连接）、事务模式（事务提交或回滚时归还连接）、语句模式（statement执行完归还连接）。 
其本身不是高可用，需使用DNS、LVS或HAProxy等方式来实现。_

   _与PgBouncer的单进程模型不同，[Pgpool-II](https://pgpool.net)使用多进程模型，仅支持客户端断开时归还连接，但支持高可用（分叉的Watchdog进程使用心跳等方式检测active节点状态，不存活时自身VIP生效成为active节点）、负载均衡（写主机、读分散）。_

   ```shell
   # 安装数据库
   sudo yum install -y postgresql-server
   # 安装json-c插件
   # 安装GEO插件(geos、gdal、proj、postgis等)
   # 安装兼容Oracle插件orafce
   # 安装java插件pljava
   # 安装审计插件pgaudit
   # 查看已安装插件
   sudo -u postgres psql
   postgres=# \dx
   postgres=# SELECT * FROM pg_extension;
   postgres=# \q
   # 安装repmgr
   sudo yum install -y repmgr
   
   # 另一个主机
   
   ```

   ```shell
   # 卸载
   # postgres=# drop extension pgaudit;
   ```

2. [Redis HA](https://redis.io/docs/management/replication)

   ```shell
   # Debian
   sudo apt-get install redis
   # sudo sh -c 'echo "maxmemory 64MB" >> /etc/redis/redis.conf'
   sudo systemctl status redis
   
   # 以同一个主机上启动2个实例示范，非同主机建议直接修改配置文件：
   # export REDIS_MASTER_HOST=114.101.100.105
   # export REDIS_MASTER_PORT=6379
   # sudo sh -c 'echo "replicaof $REDIS_MASTER_HOST $REDIS_MASTER_PORT" >> /etc/redis/redis.conf'
   redis-server --port 9736 --replicaof 127.0.0.1 6379
   
   redis-cli
   #127.0.0.1:6379>set key testValue
   #127.0.0.1:6379>quit
   redis-cli -h 127.0.0.1 -p 9736
   #127.0.0.1:9736>info replication
   #127.0.0.1:9736>get key
   #127.0.0.1:9736>quit
   ```
   
   一个master可以有多个replica，replica也可以有自己的replica，他们之间默认使用异步复制：
   连接正常时，由master向replica发送复制信息（master会记录replica id和replication offset）；
   当replica断开重连或超时时，由replica向master发出部分同步请求，master发送增量数据；
   当部分同步无法满足要求时，replica向master发出完全同步请求，此时master创建数据并发送给replica。

   master-replica复制模式下，replica能够断开后自动重连，但replica只能提供读，写会被拒绝！

   + [Redis Sentinel](https://redis.io/docs/management/sentinel)

     由于master-replica模式无法自动切换，所以需要有程序来监控master状态，并自动失败转移。一个sentinel集群可以监控多组redis！
   
     ```shell
     sudo apt-get install redis-sentinel
     sudo systemctl status redis-sentinel
     # 如果master-replica已经建立连接，安装sentinel时会自动配（/etc/redis/sentinel.conf）如下：
     #sentinel monitor mymaster 127.0.0.1 6379 2 # 注意格式为"sentinel monitor <master-name> <ip> <port> <quorum>"，quorum数量建议为"sentinel/2 + 1"
     redis-cli -p 26379
     #127.0.0.1:26379>info
     #127.0.0.1:26379>quit
     
     sudo systemctl stop redis
     # 持续执行下面的命令，会发现过一段时间才能成功（由down-after-milliseconds配置决定，默认30秒）
     redis-cli -p 9736 set key2 value2
     
     sudo systemctl start redis
     redis-cli get key2
     ```

   + [Redis Cluster](https://redis.io/docs/management/scaling)

     单个redis的缓存受实例内存限制，如果需要突破单机物理内存限制，可以使用Redis Cluster。
     Redis Cluster没有使用一致性哈希算法，而是将集群分成16384（2^14）个槽位，根据key的CRC16编码，找到对应的槽位。
     Redis Cluster支持在线增加主/从节点！

     ```shell
     sudo sh -c 'echo "cluster-enabled yes">>/etc/redis/redis.conf'
     sudo sh -c 'echo "cluster-config-file nodes.conf">>/etc/redis/redis.conf'
     # 由于sentinel做了主动切换，需要将配置（/etc/redis/redis.conf）中的配置项"replicaof"删除
     # 由于之前的实验在redis中写了数据，需要将数据清空
     sudo systemctl restart redis
     
     # 在其它主机启动redis实例后，在其中一台开始创建三主三从脚本（Redis Cluster要求至少3个主节点）
     export REDIS_CLUSTER_HOST1=114.101.100.105
     export REDIS_CLUSTER_HOST2=99.108.117.115
     redis-cli --cluster create 127.0.0.1:6379 REDIS_CLUSTER_HOST1:6379 REDIS_CLUSTER_HOST2:6379 --cluster-replicas 1
     redis-cli cluster nodes
     # 如果key计算出的CRC16值不在执行的实例上，redis会用错误信息提示槽位值及对应的节点
     redis-cli set key value
     ```

---------------------------------------
参考：

1. [CloudFoundry](https://www.cloudfoundry.org/)
2. [bitnami](https://bitnami.com/stacks)
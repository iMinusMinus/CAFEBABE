# Infrastructure as a Service

## IDC

1. 计算

   + CPU
     + CISC: x86, AMD64
     + RISC: ARM, Power, RISC-V, MIPS
   + GPU

2. 存储

   + semiconductor
     + Static Random Access Memory (SRAM) 运行速度快，容量小，成本高。场景：CPU的L1、L2缓存。
     + Dynamic Random Access Memory (DRAM) 需周期性(2ms)刷新保证数据不因电容漏电而丢失。
       + ~~Fast Page Mode (FPM)~~
       + ~~Extended Data Output DRAM (EDO DRAM)~~
       + Asynchronous DRAM (ADRAM) 不与CPU时钟同步，存在延时
       + Synchronous DRAM (SDRAM)
         + Single Data Rate Synchronous DRAM (SDR SDRAM) 主要用于FPGA
         + Double-Data-Rate SDRAM (**DDR SDRAM**)
       + Cached DRAM (CDRAM)
     + ~~PROM~~
     + ~~EPROM~~
     + EEPROM
     + Flash
       + NOR
       + NAND: SLC, MLC, TLC, QLC
         + SSD: mSATA, SATA, PCIe, M.2/NGFF: Advanced Host Controller Interface (AHCI), Non-volatile memory express (**NVMe**)
         + USB flash drive
         + Secure Digital Card (SD Card)
         + Multimedia Card (MMC)
   + magnetic
     + ~~Floppy~~
     + Tape
     + HDD: ~~Integrated Drive Electronics (IDE)/Advanced Technology Attachment (ATA)/PATA~~, ~~Small Computer System Interface (SCSI)~~, Serial ATA (**SATA**), Serial Attached SCSI (**SAS**)
   + optical 光存储不受磁场干扰，使用寿命长
     + compact discs (CD)
     + digital video discs (DVD)
     + blu-ray discs (BD)
   

3. 网络

   + TCP/IP vs ISO/OSI

     |                         | ISO/OSI            | TCP/IP               | well-known protocol                                     |
     |:------------------------|:-------------------|:---------------------|:--------------------------------------------------------|
     |                         | Application Layer  | Application Layer    | HTTP, SMTP, FTP, DNS, DHCP, NTP, SNMP, NFS              |
     |                         | Presentation Layer | Application Layer    |                                                         |
     |                         | Session Layer      | Application Layer    | TLS, SSH, NetBIOS                                       |
     | firewall                | Transport Layer    | Transport Layer      | TCP, UDP                                                |
     | router                  | Network Layer      | Internet Layer       | IP, ICMP, ARP, IPX, IGMP, RARP, OSPF                    |
     | switcher, modem, bridge | Data Link Layer    | Network Access Layer | ethernet, token ring, PPP, Frame Relay, HDLC, FDDI, ATM |
     | cables, hub, repeater   | Physical Layer     | Network Access Layer | RJ-45, IEEE 802.3                                       |

   + 传输介质
     
     |          | 布线标准           | 线型               | 最大距离         |
     |:---------|:---------------|:-----------------|:-------------|
     | ~~同轴电缆~~ | 10 BASE-2      | 细同轴电缆            | 185m         |
     | ~~同轴电缆~~ | 10 BASE-5      | 粗同轴电缆            | 500m         |
     | 双绞线      | 100 BASE-TX    | 5类双绞线            | 100m         |
     | 双绞线      | 1000 BASE-TX   | 6类双绞线            | 120m         |
     | 光纤       | 100 BASE-FX    | 9/125 µm 单模光纤    | 25km         |
     | 光纤       | 100 BASE-FX    | 62.5/125 µm 多模光纤 | 2km          |
     | 光纤       | 1000 BASE-SX   | 62.5/125 µm 多模光纤 | 275m         |
     | 光纤       | 1000 BASE-SX   | 50/125 µm 多模光纤   | 550m         |
     | 光纤       | 1000 BASE-SX   | 50 µm 多模光纤       | 1100m        |
     | 光纤       | 1000 BASE-LX   | 9/125 µm 单模光纤    | 10km         |
     | 光纤       | 1000 BASE-LX   | 62.5/125 µm 多模光纤 | 550m         |
     | 光纤       | 1000 BASE-LH   | 9/125 µm 单模光纤    | 70km         |
     | 蓝牙       | _802.15.1_     | _2.4 G_          | 2m ~ 30m     |
     | ZigBee   | _802.15.4_     | _2.4 G_          | 10m ~ 100m   |
     | WIFI     | _802.11 b/g/n_ | _2.4 G, 5 G_     | 20m ~ 200m   |
     | 微波       | -              |                  | 50km ~ 100km |
     | 微波       | -              | _卫星通信_           | 18000km      |

     _屏蔽双绞线使用金属网包裹信号线，能够抗干扰、防辐射，一般用于国防等安全性较高领域。_

     _单模光纤一般使用1310nm、1550nm波长的光波，多模光纤一般使用850nm、1310nm波长的光波。波长越小，损耗越大。_

   + [RDMA](https://www.rfc-editor.org/rfc/rfc5040)
     + [InfiniBand](https://www.infinibandta.org/about-infiniband/)
     + iWARP(RDMA over TCP/IP)
     + **R**DMA **o**ver **C**onverged **E**thernet

4. Firmware

   + [ACPI](https://uefi.org/htmlspecs/ACPI_Spec_6_4_html/) vs [Devicetree](https://www.devicetree.org/)

     ACPI提供设备静态表，并支持电源管理和一些高级特性。
   
     Devicetree为繁杂的芯片和平台进行抽象，使用设备树解决Linux源代码每增加芯片和平台就需要添加一组目录和代码的问题。
     Devicetree的源码为DTS，通过编译器DTC编译为DTB。
     Devicetree可以和Linux内核一起编译为zImage；或者由BIOS动态生成，通过传参方式传递给Linux内核。

   + [BIOS](https://www.dmtf.org/standards/smbios) vs [UEFI](https://uefi.org/)

     ~~BIOS~~ 只能引导MBR分区的硬盘，而MBR因32位地址空间有诸多限制：最多支持3个主分区和1个扩展分区（可再划分逻辑分区，但无法引导）；最大支持2TB的存储空间。

     UEFI 引导使用GPT分区的硬盘（单盘容量支持ZB级，最大支持128个分区）、支持安全启动，要求有特殊标志的FAT分区（称之为ESP，即EFI系统分区）。
     通过运行efi文件启动bootloader后加载内核，如 ~~LILO~~ 、 [GRUB](https://www.gnu.org/software/grub/) 、 [U-Boot](https://u-boot.org/) 、 Windows Boot Manager；
     或直接加载内核，如 [EFI stub](https://www.kernel.org/doc/html/latest/admin-guide/efi-stub.html) 。
     UEFI的开源实现有 [EDK II](https://github.com/tianocore/edk2) 。


## 云服务

### 虚拟化技术

虚拟化可以在不同的层次，如指令架构级（QEMU可以模拟不同CPU架构）、硬件抽象级（Xen hypervisor可产生虚拟硬件来安装操作系统）、操作系统级（容器技术）、库支持级（WINE支持在Unix上运行Windows程序）、应用程序级（JVM）

0. 硬件虚拟化
   + CPU: Intel VT-x/VPID, AMD V
   + 内存: Intel VT-x/EPT, AMD V/RVI
   + IO: SR-IOV, Intel VT-d, AMD Vi
   + Network: DPDK & SPDK, Intel VT-c

1. 服务器虚拟化

企业产品：

|          | VMWare ESXi                                   | Microsoft Hyper-V       | [XEN](https://xenproject.org/)      | [KVM](https://www.linux-kvm.org/)   |
|:---------|:----------------------------------------------|:------------------------|:------------------------------------|:------------------------------------|
| VMM      | type &#8544;(裸金属)                             | type &#8544;(mixed)     | type &#8544;(mixed: dom0, domU)     | type &#8545;(hosted)                |
| CPU虚拟化   | 硬件辅助虚拟化                                       | 硬件辅助虚拟化                 | 硬件辅助虚拟化                             | 硬件辅助虚拟化                             |
| 内存虚拟化    | SPT                                           |                         | SPT                                 | SPT，硬件辅助虚拟化                         |
| IO虚拟化    | PV, SR-IOV                                    | PV, SR-IOV              | emulation(QEMU), PV(xen-pv), SR-IOV | emulation(QEMU), PV(virtio), SR-IOV |
| 网络虚拟化    | PV(VMXNET2/VMXNET3)，硬件辅助虚拟化(VMDirectPath I/O) | emulation, PV           | PV(PV on HVM)                       | QEMU-KVM                            |
| Guest OS | Windows, Linux, FreeBSD, Solaris, MacOS       | Windows, Linux, FreeBSD | Linux, BSD, Solaris, _Windows_      | Linux, BSD, Solaris, Windows        |
| 备注       |                                               |                         | 剑桥大学XenSource开源项目，被Citrix收购         | Red Hat收购Qumranet的产品                |

Fully Virtualized (FV)，即全虚拟化，使用二进制翻译或硬件辅助虚拟化，达成运行无需修改的操作系统目的。

Paravirtualization (PV)，即超虚拟化，需要修改操作系统老支持超虚拟化API或超虚拟化驱动，性能好于全虚拟化。

Hyper-V在架构上，子分区访问I/O设备和物理内存需通过root分区，而CPU的访问可直接使用hypercall到达hypervisor：
![Hyper-V High Level Architecture](https://learn.microsoft.com/en-us/virtualization/hyper-v-on-windows/reference/media/hv_architecture.png)


家庭/个人用户产品：

|          | VMWare Workstation                          | ~~Microsoft Virtual PC~~  | [VirtualBox](https://www.virtualbox.org/) | [QEMU](https://www.qemu.org/) |
|:---------|:--------------------------------------------|:--------------------------|:------------------------------------------|:------------------------------|
| 镜像格式     | OVF, OVA                                    |                           | OVF, OVA                                  | -                             |
| 硬盘格式     | VMDK                                        | VHD                       | VDI, _VMDK, VHD, HDD_                     | QCOW2, _VDI, VMDK, VHD_       |
| 虚拟化方式    | 二进制翻译(BT) --> 硬件辅助虚拟化                       | 软件模拟，硬件辅助虚拟化              | ?                                         | 软件模拟，硬件辅助虚拟化(KVM)             |
| Host OS  | Windows, Linux                              | <= Windows 7              | Windows, Linux, macOS, and Solaris        | Windows, Linux, MacOS         |
| Guest OS | Windows, Linux, MacOS, FreeBSD, and Solaris | <= Windows 7, 32 bit only | Windows, Linux, BSD, and Solaris          | Linux, BSD                    |

_[MinGW](https://www.mingw-w64.org/) 为Windows系统提供GCC支持。[Cygwin](https://cygwin.com/) 为Windows系统提供GNU工具集和POSIX API能力。_

2. 桌面虚拟化

|       | Citrix XenDesktop | MED-V                   | VMware VDI | Red Hat |
|:------|:------------------|:------------------------|:-----------|:--------|
| 通信协议  | ICA               | RDP                     | PCoIP      | SPICE   |
| 核心虚拟件 | Xen               | Windows Server, Hyper-V | ESX        | KVM     |
| 解决方案  | VDI               | SBC                     | VDI        | VDI     |
| 备注    |                   |                         |            |         |

### 虚拟化产品

1. 云计算

   + EC2
     + 核心绑定
     + 核心超配
     + 内存超分
   + HPC
   + lambda
   + 边缘计算

2. 云存储

   + 文件存储
     + NAS
     + [OpenStack Manila](https://docs.openstack.org/manila/latest/)
   + 块存储
     + SAN
     + [OpenStack Cinder](https://docs.openstack.org/cinder/latest/)
   + 对象存储
     + [MinIO](https://min.io/)
     + [OpenStack Swift](https://docs.openstack.org/swift/latest/)

3. 云网络

   + VPC
     + 技术：Virtual eXtensible Local Area Network (VXLAN), Network Virtualization using Generic Routing Encapsulation (NVGRE)
     + 组成：Subnet, vSwitch, vRouter, security groups/ACL
     + 软件
       + [Open vSwitch](https://www.openvswitch.org/)
   + VPN
     + 按协议区分
       + UDP: ~~L2TP~~, IPSec/IKEv2, [OpenVPN](https://openvpn.net/), [WireGuard](https://www.wireguard.com/)
       + TCP: ~~PPTP~~, SSL VPN, SSTP, OpenVPN
     + 按场景区分
       + Site-to-Site VPN: ~~L2TP~~, IPSec VPN
       + Client-to-Site VPN: ~~L2TP~~, IPSec VPN, SSTP, SSL VPN, OpenVPN
   + NAT
     + Full Cone NAT, 私网IP、端口和公网IP、端口绑定，互联网的访问者都可以访问私网主机
     + Restricted Cone NAT，私网IP、端口和公网IP、端口绑定，仅被请求的节点可访问私网主机
     + Port Restricted Cone NAT，私网IP、端口和公网IP、端口绑定，仅被请求的端（IP、端口）可访问私网主机
     + Symmetric NAT，私网IP、端口及被访问的IP、端口绑定到不同的公网IP、端口，仅被请求的端（IP、端口）可访问私网主机
   + LB
     + A10 Thunder ADC
     + F5 BIG-IP iSeries
     + [VRPP](http://datatracker.ietf.org/wg/vrrp/), [keepalived](https://www.keepalived.org/)
     + [IP Virtual Server](http://www.linuxvirtualserver.org/software/ipvs.html)
        
       [LVS](http://www.linuxvirtualserver.org/) 的DR模式要求负载均衡服务和真实服务VIP相同，且在同一个子网，以便修改MAC地址后转发，真实服务的响应数据包不经负载均衡；
       NAT模式通过修改请求地址IP，进行转发，要求响应数据包也经负载均衡返回，性能略低；
       TUN模式要求真实服务支持IP隧道，通过封装数据包（封装包源IP为VIP，目标IP为真实服务IP，请求包源IP为用户IP、目标IP为VIP），允许跨子网转发到真实服务，真实服务直接响应（数据包源IP为VIP，而不是自身公网IP）。
     + [HAProxy](https://www.haproxy.org/)
     + [nginx](https://nginx.org/)
   + DHCP
     + [Kea](https://www.isc.org/kea/) 做为 [ISC DHCP](https://www.isc.org/dhcp) 系统继任者，采用MPL开源协议，支持通过REST API在线更新配置，通过MySQL或PostgreSQL存储租约等信息，通过名为Stork的管理面板来管理系统状态
     + Netgate TNSR
     + ApplianSys DNSBOX
   + DNS
     + [BIND 9](https://bind9.net/) 是C语言编写，采用MPL协议的开源DNS软件，可以运行在Linux、BSD平台。提供权威域名服务（支持从LDAP或数据库查询）和缓存递归解析结果，支持DDNS特性、GeoIP、IPv6、DoH，安全上支持DNSSEC、DNS over TLS、TSIG
     + [PowerDNS](https://www.powerdns.com/) 是C++语言编写，采用GPL协议的开源DNS软件，可以运行在Linux、Unix平台。其pdns模块提供权威域名服务（记录支持存储到数据库），pdns-recursor模块提供递归查询服务，支持DDNS特性、GeoIP、DNS64、DoH，安全上支持DNSSEC、DoT、TSIG，内建webserver提供HTTP API
     + [CoreDNS](https://coredns.io/) 是Go语言编写，采用Apache协议的开源DNS软件，可以运行在Linux、MacOS、Windows平台。提供权威域名服务和递归解析服务，支持DNS64、GeoIP、DoH、DoQ，安全上支持DNSSEC、DoT
     + [Dnsmasq](https://dnsmasq.org/) 是C语言编写，采用GPL协议的轻量级开源DNS、DHCP服务软件（适用于小型网络），可以运行在Linux、Unix平台。提供权威域名服务和缓存递归解析结果，支持Ipv6、DNSSEC
     + F5 BIG-IP
   + 网关

**云网络和传统IDC互联互通，可以使用高安全性、高成本的裸纤，也可以租用运营商支持以太网、ATM接入的MSTP网，或者更低成本的VPN技术（如介于二层和三层之间的MPLS VPN、三层的IPSec VPN）。**

### 容器

   + Open Container Initiative (OCI)
     + [containerd](https://containerd.io/)
     + [runc](https://github.com/opencontainers/runc) 为OCI规范的实现
     + [Kata Containers](https://katacontainers.io/) 是基于轻量级虚拟机的注重隔离以提高安全性的容器运行时
     + [gVisor](https://gvisor.dev/)
   + Build
     + [Buildpacks](https://buildpacks.io/)
     + [Buildah](https://buildah.io/)
     + Docker
   + [Helm](https://helm.sh/)     
   + Container Orchestration
     + [Kubernetes](https://kubernetes.io/)
     + [Apache Mesos](https://mesos.apache.org/)
     + Docker Swarm
     + HashiCorp Nomad

---------------------------------------

1. KVM

   ```shell

   ```

2. VPC

   ```shell

   ```

3. dhcpd

   ```sh
   yum install -y dhcp
   vi /etc/default/isc-dhcp-server # INTERFACEv4=ens33
   cp /etc/dhcp/dhcpd.conf /etc/dhcp/dhcpd.conf.bak
   chgrp dhcpd /etc/dhcp
   cat >> /etc/dhcp/dhcpd.conf << EOF
   default-lease-time 86400; # 默认租期置为1天
   max-lease-time     86400;
   ddns-update-style interim; 协商升级域名系统
   ignore client-updates; # 忽略客户端更新DNS记录
   option domain-name-servers 192.128.117.128,192.168.117.2; # 指定DNS服务器
   option domain-name cafe,babe;
   option routers 192.168.117.2; # 网关地址
   option time-offset 28800; 东八区与格林威治偏移时间值
   # option ntp-servers 1.2.3.4;
   # option netbios-name-servers 5.6.7.8;
   subnet 192.168.129.0 netmask 255.255.255.0 { # 子网
       range 192.168.129.129 192.168.129.192; # 指定分配的IP区间
   }
   host ae86 { # 分配固定IP
       allow booting;
       allow bootp;
       # filename /mnt/data/windows.efi; 无盘工作站用的启动文件名
       # next-server
       hardware ethernet ae:86:ca:fe:ba:be;
       fixed-address 192.168.129.192;
   }
   EOF
   ```
   
   ```shell
   # DHCP relay用于跨子网分配IP
   yum install -y dhcp-relay
   vi /etc/default/isc-dhcp-relay # SERVERS=another hdcp server, INTERFACES=ens33,virb0
   ```

4. BIND 9

   ```sh
   # CentOS 7
   # ---------------------------------------------1. Install
   yum install -y bind
   systemctl status named
   systemctl start named
   
   # 防火墙放行，避免服务器tcpdump可看见请求，请求的主机却收到响应超时
   firewall-cmd --permanent --add-service=dns
   firewall-cmd --reload
   
   # ---------------------------------------------2. Authoritative Name Server 
   # dig cafe.babe @192.168.117.128 | grep SOA | grep cafe.babe.
   # ping: cafe.babe: Name or service not known
   find / -iname named.conf -maxdepth 5
   cp /etc/named.conf /etc/named.conf.bak
   # 权威域名允许所有IP访问，named内置4个ACL：none、any、locahost、localnets
   sed -i 's|listen-on port 53 { 127.0.0.1|listen-on port 53 { any|' /etc/named.conf   
   sed -i 's|allow-query     { localhost;|allow-query     { any;|' /etc/named.conf  
   # 递归解析域名仅允许同网络访问（避免DNS放大分布式拒绝服务攻击）
   sed -i 's|recursion yes;|allow-recursion { localnets; };|' /etc/named.conf  
   # 添加权威域名
   # type: primary/master可以allow-transfer和notify给指定secondary/slave；mirror与secondary类似，但会通过DNSSEC验证后才更新，适用于建立本地根域名镜像；hint用于指定根域名服务器信息；stub仅复制ns记录；static-stub含有配置的ns记录，记录并非从primary同步而来，可能不一致；forward用于转发；redirect在常规解析返回NXDOMAIN进行替换
   # class: IN代表Internet（默认值，可省略），HS代表hesiod（MIT Athena项目的信息服务），CHAOS代表Chaosnet（MIT创建的局域网协议）
   sed -i 's|include \"\/etc\/named.rfc|zone \"cafe.babe\" IN {\n    type master;\n    file \"/var/named/cafe.babe.zone\";\n};\n\ninclude \"\/etc\/named.rfc|' /etc/named.conf
   named-checkconf
   # 添加解析zone
   # 注意配置文件不能有空格开头，否则会报错：loading from master file /var/named/cafe.babe.zone failed: no owner
   sh -c 'echo "; base zone file for cafe.babe
   \$TTL 1d    ; default TTL for zone
   \$ORIGIN cafe.babe. ; base domain-name
   ; Start of Authority RR defining the key characteristics of the zone (domain)
   @         IN      SOA   ns.cafe.babe. pi.cafe.babe. (
                                   2024033100 ; serial number，zone文件变动时应修改此序列号，便于slave同步变更
                                   1h         ; refresh，slave同步间隔，可以通过notify通知slave及时刷新
                                   60m        ; update retry
                                   7d         ; expiry，slave在指定时间仍无法连接master时，将主动删除记录
                                   24h        ; minimum，非权威DNS服务器缓存时间
                                   )
   ; name server RR for the domain
             IN      NS      ns
   ; the second name server is external to this zone (domain)
             IN      NS      ns1.eu.org.
   ; return the IPv4 address 192.168.71.18 from this zone file
   @          IN      A       192.168.117.128; ping cafe.babe
   ns        IN      A       192.168.117.128
   www        IN      A       192.168.71.18
   svc        IN      NS      ns1.he.net.; *.svc.cafe.babe权威域名服务应答委托ns1.he.net负责
   " > /var/named/cafe.babe.zone'
   chgrp named /var/named/cafe.babe.zone
   named-checkzone cafe.babe /var/named/cafe.babe.zone
   
   systemctl reload named
   systemctl restart named
   
   rndc zonestatus "cafe.babe"
   
   dig cafe.babe @192.168.117.128
   
   # ---------------------------------------------3.  Caching Name Server
   cat >> /etc/named.rfc1912.zones << EOF
   zone "eu.org" IN { // 指定该域名转发给cloudflare DNS服务器
       type forward;
       forwarders {
           1.1.1.1;
           1.0.0.1;
       };
       forward first;
   };
   EOF
   # ---------------------------------------------4. chroot
   # 以/var/named/chroot作为运行的根目录，提高安全性
   yum -y install bind-chroot
   systemctl stop named
   systemctl disable named
   systemctl start named-chroot
   ```
   
   ```shell
   # CentOS 7, IP: 192.168.117.128
   # 以下操作皆是可选操作，用于本机以自身为DNS服务器
   
   # 添加反向域名解析（ping cafe.babe）
   sed -i 's|include \"\/etc\/named.rfc|zone \"117.168.192.in-addr.arpa\" IN {\n    type master;\n    file \"/var/named/192.168.117.rev\";\n    notify no;\n};\n\ninclude \"\/etc\/named.rfc|' /etc/named.conf
   # sed -i 's|include \"\/etc\/named.rfc|zone \"71.168.192.in-addr.arpa\" IN {\n    type master;\n    file \"/var/named/192.168.71.rev\";\n    notify no;\n};\n\ninclude \"\/etc\/named.rfc|' /etc/named.conf
   
   cat > /var/named/192.168.117.rev << EOF
   ; reverse map zone file for 192.168.117.128 only
   \$TTL 2d  ; 172800 seconds
   \$ORIGIN 117.168.192.IN-ADDR.ARPA.
   @     IN      SOA   cafe.babe. hostmaster.cafe.babe. (
                                   2024033100
                                   3h        
                                   15m       
                                   3w        
                                   3h        
                                   )
   ; only one NS is required for this local file
   ; and is an out of zone name
         IN      NS      cafe.babe.
   ; other IP addresses can be added as required
   ; this maps 192.168.117.128 as shown
   128     IN      PTR     cafe.babe. ; fully qualified domain name (FQDN)
   ;128     IN      PTR     ns.cafe.babe. ; 避免与上一条冲突故注释掉
   EOF
   
   chgrp named /var/named/192.168.117.rev
   named-checkzone 117.168.192.IN-ADDR.ARPA /var/named/192.168.117.rev
   
   cat /etc/resolv.conf
   # 将DNS设为自身和原DNS IP，禁用自动DNS；虚拟机下手动分配IP可能导致宿主机无法与客户机通信
   cat >> /etc/sysconfig/network-scripts/ifcfg-ens33 << EOF
   DNS1=192.168.117.128
   DNS2=192.168.117.2
   PEERDNS=no
   EOF
   # 保存域名搜索信息，在"/etc/resolv.conf"下可以看到如下信息
   #```shell
   # # Generated by NetworkManager
   # search cafe.babe
   # nameserver 192.168.117.128
   # nameserver 192.168.117.2
   #```
   systemctl reload named
   systemctl restart named
   systemctl restart network
   ```

   ```cmd
   rem Windows 11, IP: 192.168.71.18
   nslookup
   > server 192.168.117.128
   > set type=any
   > cafe.babe
   > www.cafe.babe
   > exit
   ```

5. keepalived
    
   ```shell

   ```

---------------------------------------
参考：

1. [CloudStack](https://cloudstack.apache.org/)
2. [OpenStack](https://www.openstack.org/)
3. [RFC 4306: Internet Key Exchange (IKEv2) Protocol](https://www.rfc-editor.org/rfc/rfc4306)
4. [RFC 5996: Internet Key Exchange Protocol Version 2](https://www.rfc-editor.org/rfc/rfc5996)
5. [RFC 7348: VXLAN](https://www.rfc-editor.org/rfc/rfc7348)
6. [RFC 7637: NVGRE](https://www.rfc-editor.org/rfc/rfc7637)

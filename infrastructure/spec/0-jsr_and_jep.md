# Java Specification Requests

## JCP

Java Community Process是一个Java技术社区，以下简称JCP。

Java User Group是一个分享Java知识的志愿者团体，以下简称JUG。

JCP会员可以分成三种：
+ 准会员（Associate Members），允许为JSRs贡献并选举执行委员会（Executive Committee）的个人。

+ 合作伙伴（Partner Members），Java用户组成员或其他非盈利组织，可以选举与被选举执行委员会。

+ 正式会员（Full Members），可以担任专家组（Expert Groups）成员，领导JSRs，选举与被选举执行委员会。

个人或团体可以根据自身条件，选择成为某种类型会员。流程如下图：

![Join JCP flowchart](https://jcp.org/images/JoiningProcess2.png)

Executive Committee是一组引领JCP Java技术进化方向的团体，以下简称EC。
国内只有阿里，国外的有我们熟悉的亚马逊、Eclipse基金会、IBM、Intel、JetBrains（开发IDEA的公司）、Oracle、SAP及伦敦Java社区等。

EC的主要责任有：
1. 发展JCP提出的JSRs
2. 批准规范草案（draft specification）供公众评审（Public Review）
3. 终审完成规范和规范相关的参考实现（RIs）、技术兼容工具（TCKs）
4. 决定第一级TCK测试挑战呼吁
5. 审查和批准维护版本（maintenance releases）
6. 批准成员间转让工作
7. 为PMO提供指导

## JSR

Java规范请求是对Java平台的建议规范和最终规范的实际描述，简称JSR。

JSR由JCP会员提出，经过执委会同意后专家组便可以起草规范草案。

任何人都可以通过互联网浏览规范并提出意见，专家组根据反馈进行修订。

当新版本的草案可以作为参考实现和技术兼容工具时，专家组的主管检查完毕后便可以将其发送给执委会，执委会批准后便形成了最终版本（Final）。（在进入最终版本前，专家组主管可以撤回规范）

为响应澄清、解释、增强和修订请求，专家组会对已完成的规范、参考实现、技术兼容性工具进行更新，规范便是在维护状态（Maintenance）。

当12个月内仍有发布里程碑式草案，则规范属于有效状态（Active）。

如果规范在过去的12个月内既未发布最终版本，又未发布里程碑草案，则规范属于无效状态。

当规范没有规范/维护主管时，或者规范长时间不活动时，或规范长时间不活动被执委会投票为休眠时，规范处于休眠状态（Dormant）。

在审查阶段（JSR Reviews）、草案阶段（Early Draft Reviews、Public Reviews）、最终批准投票期间（Proposed Final Draft），执委会可以投票否决规范。

最终版本状态、维护状态、活跃状态都属于生效状态。

比如JSR-64，应提交者请求，在审查阶段被撤回规范。
比如JSR-107，在发布最终版本后（JCACHE 1.0）又发布了维护版本（JCACHE 2.0），目前属于维护状态。
比如JSR-161，仍在草案阶段，最近的草案时间为2004年，所以属于休眠状态。
比如JSR-3，2014年发布了维护4.0版本，但随之被撤回到维护草案6，目前仍属于撤回状态。
比如JSR-357，提出后被执委会否决，目前属于否决状态。

流程图如下：

![JCP 2 Procedures](https://jcp.org/images/JSR_Life_Cycle_Dec2018.png)

### 企业版Java相关重要JSR列表

| 名称                                                            | ID                 | 初始版本: 初始发布平台     | 更新版本: 更新平台（及规范）                                                                                                                                                                | 包名                                                         |
|:--------------------------------------------------------------|:-------------------|:-----------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------|
| J2EE Connector Architecture                                   | JSR 16             | 1.0: J2EE 1.2    | 1.0: <br>J2EE 1.3<br>1.5: <br>J2EE 1.4(JSR 112)<br>Java EE 5<br>1.6:<br>Java EE 6(JSR 322)<br>1.7:<br>Java EE 7(JSR 322)<br>Java EE 8                                          | javax.resource<br>javax.resource.cci<br>javax.resource.spi |
| Enterprise JavaBeans Specification                            | JSR 905            | 1.1: J2EE 1.2    | 2.0:<br> J2EE 1.3(JSR 19)<br>2.1: <br>J2EE 1.4(JSR 153)<br>3.0:<br>Java EE 5(JSR 220)<br>3.1:<br> Java EE 6(JSR 318)<br>3.2:<br>Java EE 7(JSR 345)<br>Java EE 8                | javax.ejb                                                  |
| JavaServer Pages Specification                                | JSR 906            | 1.1: J2EE 1.2    | 1.2:<br>J2EE 1.3(JSR 53)<br>2.0:<br>J2EE 1.4<br>2.1:<br>Java EE 5(JSR 245)<br>2.2:<br>Java EE 6(JSR 341)<br>2.3:<br>Java EE 7(JSR 245)<br>Java EE 8                            | javax.servlet.jsp                                          |
| Java Servlet                                                  | JSR 902<br>JSR 903 | 2.2: J2EE 1.2    | 2.3:<br>J2EE 1.3(JSR 53)<br>2.4:<br>J2EE 1.4(JSR 154)<br>2.5:<br>Java EE 5(JSR 154)<br>3.0: <br>Java EE 6(JSR 315)<br>3.1:<br>Java EE 7(JSR 340)<br>4.0:<br>Java EE 8(JSR 369) | javax.servlet<br>javax.servlet.http                        |
| Java Naming and Directory Interface Specification             |                    | 1.2.1: J2EE 1.2  | 1.2.1:<br>J2EE 1.3<br>J2EE 1.4                                                                                                                                                 | javax.naming                                               |
| JDBC Specifications                                           | JSR 910            | 2.1: J2SE 1.2    | 3.0:<br>J2EE 1.2(JSR 54)<br>J2EE 1.3(JSR 54)<br>J2EE 1.4(JSR 54)<br>4.0: <br>Java EE 6(JSR 221)<br>Java EE 7(JSR 221)<br>Java EE 8(JSR 221)                                    | java.sql<br>javax.sql                                      |
| JavaMail                                                      | JSR 904            | 1.1: J2EE 1.2    | 1.2:<br>J2EE 1.3(JSR 904)<br>J2EE 1.4(JSR 904)<br>Java EE 5(JSR 919)<br>1.4: <br>Java EE 6(JSR 919)<br>1.5:<br>Java EE 7(JSR 919)<br>1.6:<br>Java EE 8(JSR 919)                | javax.mail                                                 |
| JavaBeans Activation Framework                                | JSR 925            | 1.0.1: J2EE 1.2  | 1.0.1: <br>J2EE 1.3<br>1.0.2:<br>J2EE 1.4<br>1.1: <br>Java EE 5(JSR 925)<br>Java EE 6(JSR 925)<br>Java EE 7(JSR 925)<br>Java EE 8(JSR 925)                                     | javax.activation                                           |
| Java Transaction API                                          | JSR 907            | 1.0.1: J2EE 1.2  | 1.0.1b:<br>J2EE 1.3<br>J2EE 1.4<br>1.3:<br>Java EE 5(JSR 907)                                                                                                                  | javax.transaction                                          |
| Java Transaction Service                                      |                    | 1.0: J2EE 1.2    | 1.1:<br>J2EE 1.3                                                                                                                                                               | javax.jts(-)                                               |
| RMI over IIOP                                                 |                    | J2EE 1.2         | J2EE 1.3<br>J2EE 1.4                                                                                                                                                           | (-)                                                        |
| Java IDL API                                                  |                    | J2EE 1.2         | J2EE 1.3<br>J2EE 1.4                                                                                                                                                           | (-)                                                        |
| Java Message Service                                          | JSR 914            | 1.0.2b: J2EE 1.3 | 1.1:<br>J2EE 1.4<br>Java EE 5(JSR 914)<br>1.1: <br>Java EE 6(JSR 914)<br>2.0: <br>Java EE 7(JSR 343)<br>Java EE 8(JSR 343)                                                     | javax.jms                                                  |
| Java API for XML Processing                                   | JSR 5              | 1.0： J2SE 1.4    | 1.1:<br>(JSR 63)<br>1.2: <br>J2EE 1.4(JSR 63)<br>1.3:<br>Java EE 6(JSR 206)<br>Java EE 7(JSR 206)<br>1.6:<br>Java EE 8(JSR 206)                                                | javax.xml.parsers<br>javax.xml.transform                   |
| Java Architecture for XML Binding                             | JSR 31             | 1.0: J2SE 1.4    | 2.0: <br>Java EE 5<br> 2.2: <br>Java EE 6(JSR 222)<br>Java EE 7(JSR 222)<br>Java EE 8(JSR 222)                                                                                 | javax.xml.bind                                             |
| Java API for XML Registries                                   | JSR 93             | 1.0: J2EE 1.4    | 1.0:<br>Java EE 6(JSR 93)<br>Java EE 7(JSR 93)<br>Java EE 8(JSR 93)                                                                                                            | javax.xml.registry                                         |
| Java API for XML-based RPC                                    | JSR 101            | 1.1: J2EE 1.4    | 1.1:<br>Java EE 5(JSR 101)<br>Java EE 6(JSR 101)<br>Java EE 7(JSR 101)<br>Java EE 8(JSR 101)                                                                                   | javax.xml.rpc                                              |
| Java Authorization Contract for Containers                    | JSR 115            | 1.0: J2EE 1.4    | ?: <br>Java EE 5(JSR 115)<br>1.3: <br>Java EE 6(JSR 115)<br>1.5: <br>Java EE 7(JSR 115)<br>Java EE 8(JSR 115)                                                                  | javax.security.jacc                                        |
| SOAP with Attachments API for Java                            | JSR 67             | 1.2: J2EE 1.4    | ?:<br>Java EE 5(JSR 67)<br>1.3: <br>Java EE 8(JSR 67)                                                                                                                          | javax.xml.soap                                             |
| J2EE Management                                               | JSR 77             | 1.0: J2EE 1.4    | 1.0: <br>Java EE 5(JSR 77)<br>1.1: <br>Java EE 6(JSR 77)<br><br>Java EE 7(JSR 77)<br>Java EE 8(JSR 77)                                                                         | javax.management.j2ee                                      |
| J2EE Application Deployment                                   | JSR 88             | 1.1: J2EE 1.4    | 1.1: <br>Java EE 5(JSR 88)<br>Java EE 6(JSR 88)<br>1.2: <br>Java EE 7(JSR 88)<br>Java EE 8(JSR 88)                                                                             | javax.enterprise.deploy                                    |
| Implementing Enterprise Web Services                          | JSR 109            | 1.2: Java EE 5   | 1.3:<br>Java EE 6(JSR 109)<br>Java EE 7(JSR 109)<br>Java EE 7(JSR 109)                                                                                                         |                                                            |
| Web Services Metadata for the Java Platform                   | JSR 181            | 1.0: Java EE 5   | 2.0:<br>Java EE 6(JSR 181)<br>Java EE 7(JSR 181)<br>2.1: <br>Java EE 8(JSR 181)                                                                                                | javax.jws<br>javax.jws.soap                                |
| Java API for XML-Based Web Services                           | JSR 224            | 2.0: Java EE 5   | 2.2: <br>Java EE 6(JSR 224)<br>Java EE 7(JSR 224)<br>Java EE 8(JSR 224)                                                                                                        | javax.xml.ws                                               |
| Common Annotations for the Java Platform                      | JSR 250            | 1.0: Java EE 5   | 1.1:<br>Java EE 6(JSR 250)<br>1.2: <br>Java EE 7(JSR 250)<br>1.3:<br>Java EE 8(JSR 250)                                                                                        | javax.annotations                                          |
| JavaServer Faces                                              | JSR 127            | 1.2: Java EE 5   | 1.1:<br>(JSR 127)<br>1.2:<br>Java EE 5(JSR 252)<br>2.0: <br>Java EE 6(JSR 314)<br>2.2: <br>Java EE 7(JSR 344)<br>2.3: <br>Java EE 8(JSR 372)                                   | javax.faces                                                |
| Streaming API for XML                                         | JSR 173            | 1.0: Java EE 5   | 1.0: <br>Java EE 6(JSR 173)<br>Java EE 7(JSR 173)<br>Java EE 8(JSR 173)                                                                                                        | javax.xml.stream                                           |
| Debugging Support for Other Languages                         | JSR 45             | 1.0: Java EE 6   | 1.0: <br>Java EE 7(JSR 45)<br>Java EE 8(JSR 45)                                                                                                                                |                                                            |
| Standard Tag Library for JavaServer Pages                     | JSR 52             | 1.2: Java EE 6   | 1.2: <br>Java EE 7(JSR 52)<br>Java EE 8(JSR 52)                                                                                                                                | javax.servlet.jsp.jstl                                     |
| Java APIs for XML Messaging                                   | JSR 67             | 1.3: Java EE 6   | 1.3: <br>Java EE 7(JSR 67)                                                                                                                                                     | javax.xml                                                  |
| Expression Language                                           | JSR 245            | 2.2: Java EE 6   | 3.0: <br>Java EE 7(JSR 341)<br>Java EE 8(JSR 341)                                                                                                                              | javax.servlet.jsp.el<br>javax.el                           |
| Java Persistence                                              | JSR 317            | 2.0: Java EE 6   | 2.1: <br>Java EE 7(JSR 338)<br>2.2: <br>Java EE 8(JSR 338)                                                                                                                     | javax.persistence                                          |
| Java Authentication Service Provider Interface for Containers | JSR 196            | ?: Java EE 6     | 1.1: <br>Java EE 7(JSR 196)<br>Java EE 8(JSR 196)                                                                                                                              | javax.security.auth.container                              |
| Java Management Extensions                                    | JSR 255            | 2.0: Java EE 6   | 2.0: <br>Java EE 7(JSR 3)<br>Java EE 8(JSR 3)                                                                                                                                  | javax.management                                           |
| Bean Validation                                               | JSR 303            | 1.0: Java EE 6   | 1.1: <br>Java EE 7(JSR 349)<br>2.0: <br>Java EE 8(JSR 380)                                                                                                                     | javax.validation                                           |
| Java API for RESTful Web Services                             | JSR 311            | 1.1: Java EE 6   | 2.0: <br>Java EE 7(JSR 339)<br>2.1: <br>Java EE 8(JSR 370)                                                                                                                     | javax.ws.rs                                                |
| Dependency Injection for Java                                 | JSR 330            | 1.0: Java EE 6   | 1.0: <br>Java EE 7(JSR 330)<br>Java EE 8(JSR 330)                                                                                                                              | javax.inject                                               |
| Interceptors                                                  | JSR 318            | *1.2: Java EE 7  | 1.2: <br>Java EE 8(JSR 318)                                                                                                                                                    | javax.interceptor                                          |
| Concurrency Utilities for Java EE                             | JSR 236            | 1.0: Java EE 7   | 1.0: <br>Java EE 8(JSR 236)                                                                                                                                                    | javax.enterprise.concurrent                                |
| Contexts and Dependency Injection for Java                    | JSR 299            | 1.0: Java EE 6   | 1.1: <br>Java EE 7(JSR 346)<br>2.0: <br>Java EE 8(JSR 365)                                                                                                                     | javax.enterprise                                           |
| Batch Applications for the Java Platform                      | JSR 352            | 1.0: Java EE 7   | 1.0: <br>Java EE 8(JSR 352)                                                                                                                                                    | javax.batch                                                |
| Java API for JSON Processing                                  | JSR 353            | 1.0: Java EE 7   | 1.1: <br>Java EE 8(JSR 374)                                                                                                                                                    | javax.json<br>javax.json.spi<br>javax.json.stream          |
| Java API for WebSocket                                        | JSR 356            | 1.0: Java EE 7   | 1.1: <br>Java EE 8(JSR 356)                                                                                                                                                    | javax.websocket                                            |
| Java API for JSON Binding                                     | JSR 367            | 1.0: Java EE 8   |                                                                                                                                                                                | javax.json.bind                                            |
| Java EE Security API                                          | JSR 375            | 1.0: Java EE 8   |                                                                                                                                                                                | javax.security<br>javax.annotation.security                |
| JCache                                                        | JSR 107            | 1.0: Java EE 7   |                                                                                                                                                                                | javax.cache                                                |
| Model View Controller                                         | JSR 371            | 1.0: Java EE 8   |                                                                                                                                                                                | javax.mvc                                                  |

**EJB 3.1包含Interceptors 1.1版本*

从Java EE相关的JSR看到的很多都是集成、互操作性相关的，比如早期与CORBA打通，中期实现SOA架构相关的XML规范，目前的RESTful风格。

自Oracle于2009年收购SUN后发布了Java EE 7和Java EE 8，在2017年将JavaEE转交给了Eclipse开源社区。
但Oracle拒绝Eclipse基金会使用java名称，2018年Java EE再次更名，成了Jakarta EE。
此外，产生Jakarta EE新特性的流程也由JCP变为JESP(Jakarta EE Specification Process，基于Eclipse Foundation Specification Process v1.3修订)。

Jakarta EE目前已经发布三个版本：

1. Jakarta EE 8

   该版本与Java EE 8几乎没有任何区别，规范名称统一变为Jakarta前缀。
   **JTA**版本由1.2变更为1.3，但功能等价。
   **Deployment**版本由1.2变更为1.7（错误地将规范发布在jakarta此groupId），但功能等价。
   **Concurrency**版本由1.0变更为1.1（错误地将规范发布在jakarta此groupId），但功能等价。

2. Jakarta EE 9

   该版本将规范定义的API命名空间迁移到"jakarta.*"，规范API升了个大版本以示区分。同时，移除了一些旧的，或可选的，或废弃的规范（如JAX-RPC）。

   Jakarta EE 9.1增加了JDK 11支持。

3. Jakarta EE 10

   该版本引入了新规范CDI Lite（常搭配微服务使用），支持JDK 17。

   同时，对多个规范进行了更新（比如Servlet、EL、Authentication、Authorization、CDI、Interceptors、WebSocket、JSF及JSON相关规范等）。

## JEP

提交一个JSR通过并发布并非易事，JEP是一个途径。

JDK Enhancement-Proposal（即JEP）是openjdk组织领导的JDK增强提案。
它的主要状态为：草案（Draft）、提交（Submitted）、候选（Candidate）、完成（Completed）等。

作者可以将草案、候选流转到撤回（Withdrawn）或关闭（Closed）。

整个流程如图：

![JEP状态流转](https://cr.openjdk.java.net/~mr/jep/jep-2.0-fi.png)

### 重要JEP项目列表

| 项目名称     | JEP名称                                       | ID(target JDK)                                                               | 状态        | 成熟度       | 说明                                                |
|:---------|:--------------------------------------------|:-----------------------------------------------------------------------------|:----------|:----------|:--------------------------------------------------|
| Amber    | Pattern Matching for switch                 | JEP 406(JDK 17)<br>JEP 420(JDK 18)<br>JEP 427(JDK 19)                        | Delivered | Preview   | switch-case支持根据对象类型选择分支                           |
| Amber    | Record Patterns                             | JEP 405(JDK 19)                                                              | Delivered | Preview   | 支持record类型（JEP 395）适用JEP 394                      |
| Amber    | Sealed Classes                              | JEP 360(JDK 15)<br>JEP 397(JDK 16)<br>JEP 407(JDK 17)                        | Delivered | Final     | 密封类仅允许指定类实现/继承                                    |
| Amber    | Records                                     | JEP 359(JDK 14)<br>JEP 384(JDK 15)<br>JEP 395(JDK 16)                        | Delivered | Final     | 简化不可变数据类，类似lombok                                 |
| Amber    | Pattern Matching for instanceof             | JEP 305(JDK 14)<br>JEP 375(JDK 15)<br>JEP 394(JDK 16)                        | Delivered | Final     | instanceof判断成功块内无需类型强制转换                          |
| Amber    | Text Blocks                                 | ~~JEP 326(JDK 12)~~<br>JEP 355(JDK 13)<br>JEP 368(JDK 14)<br>JEP 378(JDK 15) | Delivered | Final     | 跨行字符串使用三个双引号包裹，无需再拼接                              |
| Amber    | Switch Expressions                          | JEP 325(JDK 12)<br>JEP 354(JDK 13)<br>JEP 361(JDK 14)                        | Delivered | Final     | 新增switch块内的箭头语法，命中分支无需break跳出，返回用yield            |
| Amber    | Local-Variable Syntax for Lambda Parameters | JEP 323(JDK 11)                                                              | Delivered | Final     | lambda表达式的局部变量可以使用类型修饰                            |
| Amber    | Local-Variable Type Inference               | JEP 286(JDK 10)                                                              | Delivered | Final     | var声明局部变量类型，编译器自动推导                               |
| Loom     | Virtual Threads                             | JEP 425(JDK 19)                                                              | Delivered | Preview   | 虚拟线程用于简化高并发编程                                     |
| Loom     | Structured Concurrency                      | JEP 428(JDK 19)                                                              | Delivered | Incubator | 简化多线程发编程                                          |
| Panama   | Foreign-Memory Access API                   | JEP 370(JDK 14)<br>JEP 383(JDK 15)<br>JEP 393(JDK 16)                        | Delivered | Incubator | JVM堆外内存管理API                                      |
| Panama   | Foreign Linker API                          | JEP389                                                                       | Delivered | Incubator | 提供一个更易用、支持更多平台、更高效的与本地代码互操作API                    |
| Panama   | Foreign Function & Memory API               | JEP412(JDK 17)<br>JEP 419(JDK 18)<br>JEP 424(JDK 19)                         | Delivered | Preview   | Foreign-Memory Access API和Foreign Linker API基础上发展 |
| Panama   | Vector API                                  | JEP338(JDK 16)<br>JEP 414(JDK 17)<br>JEP 417(JDK 18)<br>JEP 426(JDK 19)      | Delivered | Incubator | 优化向量计算                                            |
| Valhalla | Value Objects                               | NA                                                                           | Submitted | Preview   | 值类型作为引用类型，没有id和行为，可以被JVM做内存优化                     |
| Valhalla | Primitive Classes                           | JEP 401                                                                      | Candidate | Preview   | 一种特殊的值类型                                          |
| Valhalla | Classes for the Basic Primitives            | JEP 402                                                                      | Candidate | Preview   | 对包装类型和原始类型处理趋同                                    |
| Valhalla | Universal Generics                          | NA                                                                           | Submitted | Preview   | 原始类型的泛型支持                                         |
| Valhalla | Nest-Based Access Control                   | JEP 181(JDK 11)                                                              | Delivered | Final     | 内部类互访                                             |
| Valhalla | Dynamic Class-File Constants                | JEP 309(JDK 11)                                                              | Delivered | Final     | 类文件格式增加新的常量池格式                                    |
| Valhalla | JVM Constants API                           | JEP 334(JDK 12)                                                              | Delivered | Final     | 常量加载方面的新API                                       |
| Valhalla | Hidden Classes                              | JEP 371(JDK 15)                                                              | Delivered | Final     | 只可被生成类的框架反射调用                                     |
| Valhalla | Warnings for Value-Based Classes            | JEP 390(JDK 16)                                                              | Delivered | Final     | 值对象废弃API相关编译、链接警告                                 |
| Jigsaw   | The Modular JDK                             | JEP 200(JDK 9)                                                               | Delivered | Final     | 将JDK分成一系列模块，在编译、构建、运行时可以通过配置进行组合                  |
| Jigsaw   | Modular Source Code                         | JEP 201(JDK 9)                                                               | Delivered | Final     | JDK源代码分模块，在编译构建时识别模块边界                            |
| Jigsaw   | Modular Run-Time Images                     | JEP 220(JDK 9)                                                               | Delivered | Final     | JDK/JRE的运行格式改变：rt.jar变成多个jmod，新的URI加载class        |
| Jigsaw   | Encapsulate Most Internal APIs              | JEP 260(JDK 9)                                                               | Delivered | Final     | JDK内部API在编译时不可被外部访问（后续运行时也不允许）                    |
| Jigsaw   | Module System                               | JEP 261(JDK 9)                                                               | Delivered | Final     | 实现Java Platform Module System规范(JSR 376)          |
| Jigsaw   | jlink: The Java Linker                      | JEP 282(JDK 9)                                                               | Delivered | Final     | 组装模块及依赖，优化后生成自定义运行镜像                              |

Coin项目为JDK7带来了一些小的语言变更：switch支持String类型、数字字面量支持二进制字面量和下划线、多重catch、泛型类型自动推导（diamond语法）、try-with-resources语句、简化不定参数的方法调用。

Lambda项目为JDK8实现了Lambda表达式规范（JSR 335），Type Annotations项目为JDK8带来类型前可以添加注解的新语法特性（JSR 308）。

Jigsaw项目致力于JDK/JRE、库、源代码的模块化。

Amber项目是一系列java语言特性相关JEP的孵化器，Loom聚焦于重新架构JVM线程模型，Panama着眼于JVM与非Java API的互操作能力，Valhalla目标是将值对象的性能优势和面向对象抽象能力相结合。

## 参考

+ [JCP Procedures](https://jcp.org/en/procedures/overview)
+ [jsr all](https://jcp.org/en/jsr/all)
+ [javaee](https://www.oracle.com/java/technologies/javaee/javaeetechnologies.html)
+ [javaee-spec](https://javaee.github.io/javaee-spec/Specifications)
+ [Jakarta EE Specification Process](https://jakarta.ee/committees/specification/guide/)
+ [changes in jakarta ee](https://jakarta.ee/specifications/platform/10/jakarta-platform-spec-10.0.html#changes-in-jakarta-ee-8)
+ [JEP Process](https://openjdk.org/jeps/1)
+ [jep](https://cr.openjdk.java.net/~mr/jep/jep-2.0-02.html)
+ [valhalla-panama-loom-amber](https://blogs.oracle.com/javamagazine/post/java-jdk-18-evolution-valhalla-panama-loom-amber#:~:text=Much%20like%20the%20Panama%20Canal,Cimadamore%20and%20started%20in%202014.)

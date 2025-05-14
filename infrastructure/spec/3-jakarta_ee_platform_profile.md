# Jakarta EE Platform Profile

平台概要文件(platform profile)包含的以下必要规范：Activation, Authentication, Authorization, Batch, Bean Validation,
Common Annotations, Concurrency, JCA, CDI, Java Debugging Support for Other Languages, DI, EJB, EL, Interceptors,
JSONP, JSONB, Mail, Managed Beans, JMS, JPA, JAX-RS, Security, Servlet, JSF, JSP, JSTL, JTA, WebSocket。

可选规范包含：JAX-WS, SAAJ, JAXB, Enterprise Web Services。

0. EJB

__Java Beans__ 起初的设计目标是用于可视化工具里的可重用软件组件。

字段有公开的get和set方法则称之为属性(property)。
如果属性类型为数组，则称之为可索引属性(indexed property)，并可以提供带索引的get和set方法。
如果属性的值改变会产生事件，则称为绑定属性(bound property)。如果任意事件监听做出否决，值不会被修改，则被称为受限属性(constraint property)。

任何非属性定义(即属性的get和set方法)的公开方法称为java bean方法(method)。

一个bean类可以产生任意多个事件(event)，相关方法命名遵循一定格式，如事件为ActionEvent，方法为addActionListener、removeActionListener。监听器必须是java.util.EventListener子类。

~~Managed Beans~~ 规范定义了受Jakarta EE容器管理的应用组件其基本的编程模型，大部分java类（要求非内部类、非抽象类、非jakarta.enterprise.inject.spi.Extension实现、所在包任意类都没有jakarta.enterprise.inject.Vetoed注解标记、有无参构造函数或构造函数参数有Inject注解），包括EJB都属于Managed Beans（即MBean?）。
Managed Beans支持资源注入、生命周期回调以及拦截器。

*CDI完整环境，有Decorator注解的抽象类也属于Managed Bean！*

__EJB__ 即Enterprise JavaBeans，实现EJB技术的Jakarta EE组件称之为Enterprise beans。
它屏蔽了事务管理、多线程、连接池、安全等低级API细节，简化了大型、分布式应用的开发工作。

Enterprise Beans架构定义了三种类型对象：会话bean(Session Bean)、消息驱动bean(Message-Driven Bean)、实体bean(Entity Bean，可选，只能打包在ear)。

Session bean代表一个单一的客户端执行，它生命周期相对短暂（通常与交互式会话绑定，数据不会持久化）、可以与事务相关、可以修改数据库的共享数据，可以实现web service。
当容器崩溃时，它也会被移除；当客户端重新连接时，它被重新生成。
Session bean可以根据是否保存会话状态分为stateful session bean和stateless session bean，新规范还定义了singleton session bean。
Session bean可以有三种客户端：remote client(可以是部署在同一个容器或不同容器的session bean，也可以是java application、applet、servlet)， local client(必须同一JVM，可以是其它Enterprise Beans或web组件)， webservice client(无状态会话bean或单例会话bean才存在)。
新版本规范中，客户端可以通过jakarta.ejb.EJB或JNDI(javax.annotation.Resource, jakarta.xml.ws.WebServiceRef)直接注入业务接口。

旧版本规范要求客户端先获取session bean的home接口(新版本兼容用jakarta.ejb.RemoteHome或jakarta.ejb.LocalHome注解；旧版本需继承jakarta.ejb.EJBHome或jakarta.ejb.EJBLocalHome)，home接口通常定义了create、find、remove方法。
通过home接口再得到业务接口(新版本称之为component interface，使用jakarta.ejb.Remote或jakarta.ejb.Local注解；旧版本为local或remote，需继承jakarta.ejb.EJBObject或jakarta.ejb.EJBLocalObject)的引用。
开发者编写的session bean(新版本需要jakarta.ejb.Stateful或jakarta.ejb.Stateless或jakarta.ejb.Singleton注解，一般实现业务接口；旧版本要求实现jakarta.ejb.SessionBean，没有直接实现业务接口，但有业务接口的实现方法)。
Session bean支持钝化(passivate)与激活(activate)，即保存到次级存储或从次级存储恢复。
当操作发生时容器会触发相应回调，开发者可以通过jakarta.ejb.PrePassivate和jakarta.ejb.PostActivate来处理。
此外，可以使用注解来标记会话bean的生命周期：create(jakarta.ejb.Init)、remove(jakarta.ejb.Remove)等。
特别的，对于无状态会话bean，在事务开启或结束前后，可以通过jakarta.ejb.AfterBegin、jakarta.ejb.BeforeCompletion、jakarta.ejb.AfterCompletion注解来注册回调。

*旧规范的命名约定以业务接口名为BusinessDomain举例，则home接口为BusinessDomainHome，对应的session bean命名为BusinessDomainBean。*

Message-Driven Bean为应用提供异步处理消息的能力，它是无状态的，可以是事务相关，可以更新数据库共享数据，通常充当JMS消息监听器，EJB容器负责创建对应的consumer实例。
旧规范要求消息驱动bean实现MessageDrivenBean接口和MessageListener接口，新规范使用MessageDriven注解并实现MessageListener接口即可。

*session bean同步方式发布或订阅消息，而message-driven bean是异步的。*

Entity Bean是领域模型，为数据库数据提供对象视图，生命周期通常较长。已提交事务的实体不会因容器崩溃而导致实体及主键的信息丢失。
旧规范中Entity Bean的持久化可以由容器管理（Container-Managed Persistence）或开发者管理（Bean-Managed Persistence）。
客户端获取实体bean的引用同会话bean一样需要通过home接口来创建组件接口，实体作为抽象类需实现EntityBean，需直接或间接实现TimedObject，必须有无参构造函数，而无需实现组件接口。
新规范则完全使用JPA替换CMP和BMP，实体只需使用Entity注解。

对于无状态会话bean和消息驱动bean可以设置超时时间(jakarta.ejb.Timeout)。

此外，可以通过jakarta.ejb.Schedule来创建定时任务。


1. JMS

JMS即Java Message Service API，为开发者提供了异步创建、发送及接收消息的通用方式。其参考实现为Eclipse OpenMQ。

JMS支持两种风格的消息：通过队列(queue)作为中介的点对点(point-to-point)方式消息，通过主题(topic)作为中介的发布订阅(pub/sub)方式消息。
发布订阅方式下一个客户端发送的消息可以被投递到多个客户端。

旧规范的API较为复杂，需要通过ConnectionFactory创建Connection，通过Connection创建Session，通过Session可以创建Message、MessageProducer或MessageConsumer。
Producer将消息发送到Destination，Consumer从Destination接收消息。

新规范允许直接从ConnectionFactory创建JMSContext，通过JMSContext创建Message、JMSProducer或JMSConsumer。

|               | JMS                                                                             | spring-cloud-stream & spring-messaging                                                  | 备注                                                                                     |
|:--------------|:--------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------|
| 定义消息连接        | jakarta.jms.JMSConnectionFactoryDefinition                                      |                                                                                         |                                                                                        |
| 注入消息上下文       | jakarta.jms.JMSConnectionFactory                                                |                                                                                         | JMS通过Inject注解和JMSConnectionFactory合用可注入JMSContext，可通过Resource注入ConnectionFactory       |
| 发布者           | *jakarta.jms.JMSProducer*                                                       | *java.util.function.Supplier<br>org.springframework.cloud.stream.function.StreamBridge* | spring-cloud-stream配置形如"--spring.cloud.stream.bindings.{bean}-out-{index}.destination" |
| 订阅者           | *jakarta.jms.JMSConsumer*                                                       | *java.util.function.Consumer*                                                           | spring-cloud-stream配置形如"--spring.cloud.stream.bindings.{bean}-in-{index}.destination"  |
| topic         | *jakarta.jms.Topic*                                                             |                                                                                         | JMS通过Resource注解注入topic；spring-cloud-stream使用配置                                         |
| queue         | *jakarta.jms.Queue*                                                             |                                                                                         | JMS通过Resource注解注入queue                                                                 |
| 消息            | *jakarta.jms.Message*                                                           |                                                                                         | JMS消息分为消息头和消息体；spring-cloud-stream支持使用POJO作为消息，框架根据媒体类型自动序列化/反序列化                      |
| 事务            | jakarta.ejb.TransactionAttribute                                                |                                                                                         |                                                                                        |
| 安全            | jakarta.jms.JMSPasswordCredential                                               |                                                                                         |                                                                                        |
| 消费策略          | jakarta.jms.JMSSessionMode                                                      |                                                                                         | spring-cloud-stream支持拉取(pull)；RocketMQ支持批量拉取，支持并发消费和顺序消费                               |
| 生产策略          | *jakarta.jms.JMSProducer.setDeliveryMode*                                       |                                                                                         | JMS同时支持同步发送和异步发送，Kafka支持批量发送                                                           |
| 发送异常处理        | *jakarta.jms.CompletionListener*                                                |                                                                                         |                                                                                        |
| broker投递策略    | *at-most-once<br>once-and-only-once*                                            |                                                                                         |                                                                                        |
| 优先级           | *jakarta.jms.Message.setJMSPriority*                                            |                                                                                         |                                                                                        |
| 延时投递          | *jakarta.jms.JMSProducer.setDeliveryDelay*                                      |                                                                                         |                                                                                        |
| 死信            | -                                                                               |                                                                                         |                                                                                        |
| 过期时间          | *jakarta.jms.JMSProducer.setTimeToLive<br>jakarta.jms.Message.setJMSExpiration* |                                                                                         |                                                                                        |
| request-reply | *jakarta.jms.Message.setJMSCorrelationID<br>jakarta.jms.Message.setJMSReplyTo*  |                                                                                         |                                                                                        |
| wire protocol | -                                                                               | *org.springframework.messaging.converter.MessageConverter*                              |                                                                                        |
| 负载均衡          | -                                                                               |                                                                                         |                                                                                        |
| 消息筛选          | __SQL92__                                                                       |                                                                                         | JMS允许客户端通过消息头使用SQL92子集来过滤感兴趣的消息                                                        |

JMS作为API标准，无法跨语言使用。与JMS不同的是AMQP定义协议报文，支持多种客户端，支持direct、fanout、topic、header四种消息收发模型。
spring-cloud-stream的分区、死信队列、负载均衡等依赖提供的Binder(Kafka, RabbitMQ等)。

对于分布式消息，需要使用JMS对应的XA接口！

对于一个消息中间件，它的客户端可能同时支持同步/异步发送消息，由用户决定性能优先还是可靠性优先。对于异步发送，通常还会允许批量发送，来提升性能。
而broker可以选择是直接返回成功，还是持久化消息到磁盘，甚至指定个数副本复制成功才返回，性能和可靠性由中间件管理员和开发者决定。
对于消息投递，broker可以选择最多投递一次，或者至少投递一次（即重试至成功或达到重试次数上限）。对于投递恰好一次，由于网络、应用等均可能存在故障，难以保证。
至少投递一次下，broker在消费者客户端确认后才可以将消息移除或标记为已消费。
消费者的客户端有主动pull的，此种方式下消费者可以根据自身线程繁忙程度来决定拉取频率。也有的broker采取push方式，此种方式下消费者能够更及时收到消息。
pull模式一般还会批量拉取消息，此时还会面临是并发消费还是顺序消费的问题。
有些时候，消费者可能长期出现问题，或者产生的消息设置存在问题，导致消息长时间没有被消费，有些broker允许消息设置过期时间来移除，或者转移到特殊的地方，供人工处理。
broker的存储也不是无限的，一般会采取保留最近时间的消息，或最近指定数量的消息。
消费者和生产者的数量可能不固定，或者状态发生变更，需要有自动平衡能力，来保障消息均衡生产/消费。


__JMS预定义了请求头：JMSDestination、JMSDeliveryMode、JMSMessageID、JMSTimestamp、JMSCorrelationID、JMSReplyTo、JMSRedelivered、JMSType、JMSExpiration、JMSPriority。__

2. Batch

Batch规范定义了一个可以在XML中编排批处理任务的Java API和基于XML的JSL(job specification language)。它的参考实现为IBM JBatch。

规范指定由JobOperator来启动任务，任务元数据来源于JobRepository，一个Job有许多的Step，每个Step可以是ItemReader、ItemProcessor、ItemWriter其一。
每个Job每次执行有对应的JobInstance和JobParameters，每个JobInstance有对应的一个或多个(restart)JobExecution来记录执行信息。

|        | Batch                                                  | spring-batch                                                 | 备注                                                        |
|:-------|:-------------------------------------------------------|:-------------------------------------------------------------|:----------------------------------------------------------|
| 任务启动器  | *jakarta.batch.operations.JobOperator*                 | *org.springframework.batch.core.launch.JobLauncher*          |                                                           |
| 任务     | &#60; job id="" restartable="true" /&#62;              | *org.springframework.batch.core.Job*                         |                                                           |
| 任务上下文  | *jakarta.batch.runtime.context.JobContext*             | *org.springframework.batch.core.context.context.JobContext*  |                                                           |
| 任务监听   | *jakarta.batch.api.listener.JobListener*               | *org.springframework.batch.core.JobExecutionListener*        | spring-batch支持使用注解在任务前后、异常时处理                             |
| 步骤     | &#60; step id="" /&#62;                                | *org.springframework.batch.core.Step*                        |                                                           |
| 步骤上下文  | *jakarta.batch.runtime.context.StepContext*            | *org.springframework.batch.core.context.context.StepContext* | spring-batch定义了JobScope、StepScope                         |
| 步骤监听   | *jakarta.batch.api.listener.StepListener*              | *org.springframework.batch.core.StepListener*                |                                                           |
| 获取数据   | *jakarta.batch.api.chunk.ItemReader*                   | *org.springframework.batch.item.ItemReader*                  |                                                           |
| 获取数据监听 | *jakarta.batch.api.chunk.listener.ItemReadListener*    | *org.springframework.batch.core.ItemReadListener*            | Batch/spring-batch支持重试、跳过监听，spring-batch支持使用注解在读取前后、异常时处理 |
| 处理数据   | *jakarta.batch.api.chunk.ItemProcessor*                | *org.springframework.batch.item.ItemProcessor*               |                                                           |
| 处理数据监听 | *jakarta.batch.api.chunk.listener.ItemProcessListener* | *org.springframework.batch.core.ItemProcessListener*         | Batch/spring-batch支持重试、跳过监听，spring-batch支持使用注解在处理前后、异常的监听 |
| 保存数据   | *jakarta.batch.api.chunk.ItemWriter*                   | *org.springframework.batch.item.ItemWriter*                  |                                                           |
| 保存数据监听 | *jakarta.batch.api.chunk.listener.ItemWriteListener*   | *org.springframework.batch.core.ItemWriteListener*           | Batch/spring-batch支持重试、跳过监听，spring-batch支持使用注解在写前后、异常时处理  |
| 分片     | *jakarta.batch.api.partition.PartitionMapper*          |                                                              |                                                           |

Batch规范通过在XML配置一个批处理任务的开始(start)、执行下一步(next)、条件选择(&#60;next on="{exit status}" to="{id}" /&#62;)、终止(stop)、结束(end)、成功、失败(fail)，及流程(flow)、分叉(split)、决策(decision)这些特殊步骤。
而spring-batch完全通过构建任务时通过Java API指定。

__spring-batch也实现了Batch规范。spring-batch定义了批处理任务的表结构。__

3. Activation & JavaMail & JCA

JAF即Java Activation Framework，Activation规范允许从任意数据源(DataSource)通过DataContentHandler知悉MIME类型和访问内容，支持发现JavaBeans的可用操作(CommandInfo)并实例化。

JavaMail规范为电子邮件及消息传递定义了平台无关、协议无关的框架。开发者可以通过建立会话(Session)来获取信箱(Store)，
通过指定收件箱(Folder)及对应的位置得到信件(Message)，再获取信件主题、内容和附件(如果存在)等信息。
也可以创建信件，指定收件人地址(Address)，然后从会话获取传输工具(Transport)投递出去。

JCA即Java Connector Architecture，新版本也称之为Connectors。
该规范定义企业Java应用组件与EIS(Enterprise Information Systems，如ERP、交易处理系统、遗留的数据库系统)系统互联的标准架构。
该架构包含两部分，一个由EIS产商提供的资源适配器(resource adapter)，一个允许资源适配器插入的应用服务器(application server)。
架构定义了诸如事务(transaction)、安全(security)、连接管理(connection management)、资源适配器生命周期管理(lifecycle management)、任务管理(work management)的一系列约定，供应用服务器和EIS系统通过资源适配器进行双向通信。
部署在应用服务器的应用组件可以通过通用客户端接口(Common Client Interface)与资源管理器交互，或JDBC接口与JDBC驱动交互。



参考：
1. [Jakarta EE Platform 10](https://jakarta.ee/specifications/platform/10/)
2. [EJB Lite](https://jakarta.ee/specifications/enterprise-beans/4.0)
3. [Managed Bean](https://jakarta.ee/specifications/managedbeans)
4. [JMX MBean](https://docs.oracle.com/javase/tutorial/jmx/overview/index.html)
5. [JMS](https://jakarta.ee/specifications/messaging/3.0)
6. [Connectors](https://jakarta.ee/specifications/connectors/2.0)
7. [Activation](https://jakarta.ee/specifications/activation/2.0)
8. [JavaMail](https://jakarta.ee/specifications/mail/2.0)
9. [Batch](https://jakarta.ee/specifications/batch/2.0)
10. [JAKARTA EE COMPATIBLE PRODUCTS](https://jakarta.ee/compatibility/certification/10/)
11. [JavaBeans](https://www.oracle.com/java/technologies/javase/javabeans-spec.html)
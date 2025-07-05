# 值得关注的规范

1. JCache

|         | JCache                                                               | spring-context                                                   | 备注                                                                   |
|:--------|:---------------------------------------------------------------------|:-----------------------------------------------------------------|:---------------------------------------------------------------------|
| 缓存key   | javax.cache.annotation.CacheKey                                      | org.springframework.cache.annotation.Cache*(key=)                | spring-cache的CacheKey支持SpEL                                          |
| 缓存无则执行  | javax.cache.annotation.CacheResult                                   | org.springframework.cache.annotation.Cacheable                   | JCache允许不检查缓存总是执行，spring-cache支持避免多线程同时加载相同key                       |
| 执行后缓存   | javax.cache.annotation.CachePut<br>javax.cache.annotation.CacheValue | org.springframework.cache.annotation.CachePut                    | JCache默认将非CacheValue标记的参数作为CacheKey                                  |
| 移除单个key | javax.cache.annotation.CacheRemove                                   | org.springframework.cache.annotation.CacheEvict                  | JCache/spring-cache可以选择执行前移除缓存                                       |
| 缓存移除    | javax.cache.annotation.CacheRemoveAll                                | org.springframework.cache.annotation.CacheEvict(allEntries=true) |                                                                      |
| 异常处理    | javax.cache.annotation.Cache*(cacheFor={}, noCacheFor={})            | org.springframework.cache.annotation.Cache*(condition=, unless=) |                                                                      |
| 缓存配置    | javax.cache.annotation.CacheDefaults                                 | org.springframework.cache.annotation.CacheConfig                 | JCache/spring-cache支持在类上配置cacheName、CacheResolver、CacheKeyGenerator  |

从注解和接口设计来看，JCache和spring-cache十分相似，都支持类级别配置缓存的cacheName、CacheResolver、CacheKeyGenerator，也允许在方法级别上进行覆盖。

在方法级别的注解上，都支持特定条件去取/存/清除缓存。但方式上有区别，体现在spring-cache以容器为核心，CacheResolver、CacheKeyGenerator这种配置都是配置容器内的bean id，对于条件的处理则是依赖SpEL的支持。
而JCache设计时并没有过多的考虑接纳DI/CDI，在条件上则仅考虑异常来二分。
值得注意的是，JCache在扩展性上考虑到了缓存创建、更新、失效、移除的事件监听。
此外，JCache虽不能直接配置每个key的失效策略，但有API（javax.cache.expiry.ExpiryPolicy）来规范这些行为。

spring-cache也实现了JCache，底层支持的本地缓存可以是caffeine、cache2k、~~Guava cache~~、ehcache，分布式缓存可以是Infinispan、Hazelcast、Couchbase、 Redis等。

2. JMX

JMX即Java Management Extensions，属于Java SE平台的一部分，它提供了一个简单、标准的监控、管理JVM和应用程序的方式。

JMX规范定义了5种类型的MBean：Standard MBeans为实现类与接口类名称遵循一定格式（接口类名形如NameMBean，实现类则为Name），接口定义可读写的属性和可调用的操作；
MXBeans与Standard MBeans类似，不同之处在于接口后缀为MXBean，或使用MXBean注解标记时可以不需要此后缀；
Dynamic MBeans定义了DynamicMBean作为通用的动态MBean接口，实现类可以有选择的暴露属性和操作（甚至可以是类并不存在的属性或方法），而其他私有和公有方法或字段对JMX体系不可见；
Model MBeans也是一种Dynamic MBeans，它可以桥接管理接口和托管资源，特殊之处在于它支持持久化、通知、属性值缓存、操作代理；
Open MBeans也是一种Dynamic MBeans，特殊之处在于它通过一系列预定义的java类（如包装类、TabularData、CompositeData）来支持非java程序的远程管理端。

|                | JMX                                          | micrometer                                         | dropwizard-metrics                    | JavaMelody                        |
|:---------------|:---------------------------------------------|:---------------------------------------------------|:--------------------------------------|:----------------------------------|
| 协议             | RMI, _SNMP_                                  | http                                               | -                                     | -                                 |
| 监控             | *javax.management.monitor.Monitor*           | Meter                                              | Metric                                | -                                 |
| 监控指标: Gauge    | *javax.management.monitor.GaugeMonitor*      | Gauge, TimeGauge                                   | Gauges                                | -                                 |
| 监控指标: Counter  | *javax.management.monitor.CounterMonitor*    | Counter, FunctionCounter                           | Counters                              | Counter                           |
| 监控指标: Timer    | -                                            | Timer, LongTaskTimer, FunctionTimer                | Timers                                | -                                 |
| 监控指标: other    | *javax.management.monitor.StringMonitor*     | DistributionSummary                                | Meter, Histograms                     | Histogram                         |
| 监控对象处理         | -                                            | *io.micrometer.core.instrument.config.MeterFilter* | *com.codahale.metrics.MetricFilter*   | -                                 |
| 模式             | push                                         | push                                               | push                                  | push: DataDog, Graphite, InfluxDB |
| registry/agent | *javax.management.MBeanServer*               | *io.micrometer.core.instrument.MeterRegistry*      | *com.codahale.metrics.MetricRegistry* | -                                 |
| 监控面板           | JConsole                                     | -                                                  | console, SLF4J, CSV                   | Web UI                            |
| 标记为指标信息提供者     | javax.management.MXBean                      | @Counted<br>@Timed                                 | -                                     | net.bull.javamelody.Monitored     |

可以看到各种度量框架一般会分为当前值（Gauges）、累计值（Counters）、耗时（Timer）。

JMX注册的ObjectName形式为"${domain:*}:type=${type},name=${name}"，JMX支持统计值超出阈值范围时发出通知。

[Jolokia](https://jolokia.org) 作为JMX-HTTP桥接器，通过http/servlet容器，将信息以HTTP协议暴露出去。
Jolokia支持两种模式，agent模式（javaagent、war、osgi）和proxy模式（JSR-160 remote connector），架构图分别如下：

![jolokia_architecture](https://jolokia.org/images/jolokia_architecture.png)
![proxy mode](https://jolokia.org/reference/html/manual/_images/proxy.png)

[JMiniX](https://github.com/lbovet/jminix) 是和Jolokia类似的一款JMX-HTTP桥接工具，它只能以war方式部署。

[~~MX4J~~](https://mx4j.sourceforge.net) 是一款开源的JMX实现和扩展，它支持从XML读取并加载MBean、以HTTP协议暴露信息等功能。

micrometer作为监控界相当于日志界SLFJ的门面框架，支持多种数据采集/存储工具，还定义了DistributionSummary（带次数和累计信息）、LongTaskTimer（记录长时间任务的执行任务数、持续时间）、TimeGauge（有单位的瞬时值）。
micrometer提供的Counted、Timed注解，在spring框架下通过AOP来发生效应。

dropwizard-metrics还定义了Meter（累计值和均值，如最近1分钟、5分钟、15分钟均值）、Histograms（累计值和近期多个瞬时值）。

JavaMelody基于Filter、Interceptor或Spring AOP手段，对http、sql、jvm等信息进行监控。

spring-boot-actuator可以使用JMX形式或HTTP端点形式管理和监控应用程序，它还支持采集和输出指标、审计、http链路跟踪、健康探测（提供了就绪检查、存活检查，可以很好的与Kubernetes结合）。
spring-boot-actuator支持对Jolokia（一个JMX-HTTP桥接框架）、micrometer进行开箱即用。
spring-boot-actuator与JMX都提供了读、写属性的能力，JMX还提供了操作的能力，但spring为JMX端点和http端点提供了一个统一的门面。

__JMX不只可以做监控（读取属性:DynamicMBean.getAttribute），也可以管理（修改属性:DynamicMBean.setAttribute，执行操作:DynamicMBean.invoke，产生通知）。__

3. Rule Engine

JSR 94定义了简单的API，供Java SE和Java EE客户端访问规则引擎，包括注册/取消注册规则、解析规则、获取规则元数据、执行规则、获取规则执行结果、对结果进行过滤。
其参考实现基于Jess(Java Expert System Shell)规则引擎。

Java Content Repository (JCR)现由JSR 170和JSR 283组成，该规范定义了一个访问内容仓库（非结构化数据）的标准方式。

|          | Drools                     | URule              | OpenL Tablets                | 备注                    |
|:---------|:---------------------------|:-------------------|:-----------------------------|:----------------------|
| 协议       | Apache                     | Apache, Commercial | LGPL                         | URule开源版本功能较少         |
| JSR94兼容  | Y                          | N                  | N                            |                       |
| JSR283兼容 | N                          | Jackrabbit         | Y                            |                       |
| GUI      | Drools Workbench           | urule-console      | WebStudio                    |                       |
| 规则集      | when-then                  | 规则集                | rule = conditions + actions  | Drools支持DMN FEEL      |
| 决策流      | DMN DRD                    | 规则流                | N                            |                       |
| 决策树      | Decision Tree              | 决策树                | Decision Tree                |                       |
| 决策表      | DMN Decision Table         | 决策表，_决策矩阵_         | Decision Table, Lookup Table |                       |
| 函数集      | Y                          | Y                  | Y                            |                       |
| 评分卡      | Spreadsheet Decision Table | 评分卡                | Spreadsheet Table            |                       |
| DSL      | DRL, DSL                   | DSL                | Java, BEX                    | URule支持中文的脚本，由ANLTR解析 |
| 规则匹配算法   | RETEoo                     | RETE               |                              |                       |

表达式语言(EL)和脚本引擎(ScriptEngine)在一定程度上可以替代规则引擎，都仍属于开发者工具范畴，无法直接将自然语言直接当作规则来执行，离业务人员使用有一定距离。

商业规则引擎除Jess外，还有应用服务器供应商产品如IBM ILOG JRules等，行业巨头产品如FICO Blaze Advisor等。
独立的规则引擎厂商如OpenRules（支持DMN，如规则放在SpreadSheet），开源的~~JLisa~~、RuleBook、EasyRule（基于表达式语言MVEL/JEXL/SpEL）、rule4j、~~Mandarax~~。

4. JPMS

Jigsaw项目的产物为JPMS(Java Platform Module System)，在JDK9及以上版本内置。

|         | JPMS                                  | OSGi                        | 备注                                                 | 
|:--------|:--------------------------------------|:----------------------------|:---------------------------------------------------|
| 模块依赖    | module-info.java: requires            | MANIFEST.MF: Import-Package |                                                    |
| 依赖透明传递  | module-info.java: requires transitive | __不支持__                     |                                                    |
| 依赖多版本共存 | __不支持__                               | __支持__                      | OSGi中每个bundle有自己的类加载器                              |
| 依赖分类    | __不支持__                               | __不支持__                     | maven分为compile, runtime, test, provided; optional  |
| 限定导出    | module-info.java: exports             | MANIFEST.MF: Export-Package |                                                    |

JPMS有如下几个好处：

   + rt.jar变成了多个jmod，方便裁剪JRE，缩小云原生镜像打包体积
   + 通过module-info.java的opens和exports语法，可以约束外部使用者（当然也会受依赖的模块约束）
   + 得益于module-info.java的opens语法，JVM可以加快搜索类的速度
   + 由于module-info.java的requires语法，同类名在不同模块的冲突可以得到解决

要享受到JPMS的好处，先需要进行改造。

如果对应到Maven上，简单地说就是一个maven模块一个module-info.java文件，
而且module-info.java文件必须在源代码根目录下，一般是"src/main/java"目录下。

以下是一个module-info.java的示例及说明：

```java
/* open */ // 模块下所有包都开放
module module.name { // 命名规则和包名类似，建议使用符合命名要求的Maven artifactId或java package
   requires java.base; // 依赖的模块名
   requires transitive org.slf4j; // 依赖本模块的模块，无需再声明依赖org.slf4j模块
   requires static option.al; // 编译期需要的依赖，运行时可选
   requires org.slf4j; // 对于尚未模块化(unnamed module)的依赖，JPMS从模块定义中无法加载时，回退到classpath进行加载
   
   exports p.a.c.k.a.g.e; // 允许所有模块访问指定package
   exports pack.age to other.pack.age, another.pack.age; // 仅允许指定模块访问指定package
   
   opens module.name.sub; // 允许"module.name.sub"包运行时被访问（即反射）
   opens module.name.child to p.k.g, pack.age; // 仅允许指定模块可以反射指定包
   
   uses s.p.i; // 模块需要使用到SPI的实现
   
   provides s.p.i with c.l.a.s.s; // 声明模块的类实现的SPI
}
```

模块化后，也会带来坏处，主要是兼容上的影响：

   + ClassLoader::getResource*, Class::getResource*

     对于JDK内部资源，无法再通过类加载形式获取。

     模块私有资源被限制在模块内，需要改用Module::getResourceAsStream形式，或者使用"jrt:"格式。

   + META-INF/services

     SPI实现已经在module-info.java声明了，旧形式可免除。

   + AccessibleObject::setAccessible

     模块化前可以通过setAccessible来访问方法或字段，现在必须先开放给访问包。

   + URLClassLoader

     sun.misc.Launcher.AppClassLoader和sun.misc.Launcher.ExtClassLoader被移除，
     取而代之的jdk.internal.loader.ClassLoaders.AppClassLoader、jdk.internal.loader.ClassLoaders.PlatformClassLoader并不是URLClassLoader的子类。

常见问题：

   + 单元测试

     通常单元测试会引入JUnit、Mockito一类的框架，在Maven中会被声明scope为test，即正式打包时不会包含单元测试框架。
     而模块化后，我们的module-info.java自然不会包含junit等测试依赖包。

     当然，我们可以给测试目录也模块化，但会产生很多问题：
     比如破坏约定的结构（因不同模块不能有相同包名，测试类和被测类无法在同一个包下）、被测类原package方法需要反射才能被测试类调用等。

     如果想保持原来的形式，需要IDE支持，使得test目录按旧的classpath方式编译、运行测试，以免出现包引入、编译/运行出错。
     此外，需要合适版本的构建插件（如maven-compiler-plugin、maven-surefire-plugin）保障源码模块化后的编译和测试源码以传统方式编译测试进行。

   + 运行
 
     很多框架是通过反射来完成DI、AOP，而我们通常没有那么清楚哪些包需要开放给那些模块，只能在运行时根据报错添加导出(--add-exports)、开放(--add-opens)的包。

   + 自动模块包引用冲突

     如果从多个自动模块（即JDK9前没有module-info.java的jar被JDK自动以jar名称来生成模块名）读取相同的包，则会发生包冲突（但p.k.g.a和p.k.g.b不会冲突）。
     
     此时，只能等库作者要么将多个库进行合并，或者将库使用不同的包名。

5. SPI

|        | SPI                          | spring                                                      | dubbo                                                      | shardingsphere                                              |
|:-------|:-----------------------------|:------------------------------------------------------------|:-----------------------------------------------------------|:------------------------------------------------------------|
| 目标     | interface or abstract class  | class(usually interface or annotation)                      | interface marked with dubbo SPI annotation                 | interface or abstract class                                 |
| 实现配置文件 | META-INF/services/{s.p.i}    | META-INF/spring.factories                                   | META-INF/dubbo/{s.p.i}, META-INF/services/{s.p.i}          | META-INF/services/{s.p.i}                                   |
| 配置文件格式 | {c.l.a.s.s#optional comment} | {spring.annotation.fqcn=impl.fqcn,impl.f.q.c.n}             | {f.q.c.n#optional comment} or {name=f.q.c.n}               | {c.l.a.s.s#optional comment}                                |
| 核心类    | *java.util.ServiceLoader*    | *org.springframework.core.io.support.SpringFactoriesLoader* | *org.apache.dubbo.common.extension.ExtensionLoader*        | *org.apache.shardingsphere.spi.ShardingSphereServiceLoader* |
| 优先级    | NA                           | org.springframework.core.annotation.Order                   | __org.apache.dubbo.common.lang.Prioritized__               | __org.apache.shardingsphere.spi.ordered.OrderedSPI__        |
| 限定名称   | NA                           | org.springframework.beans.factory.annotation.Qualifier      | Y                                                          | -                                                           |
| 依赖条件   | NA                           | org.springframework.context.annotation.Conditional          | org.apache.dubbo.common.extension.Activate(group=, value=) | NA                                                          |
| 依赖注入   | NA                           | -                                                           | __org.apache.dubbo.common.extension.ExtensionFactory__     | NA                                                          |
| 单例     | NA                           | -                                                           | Y                                                          | Y                                                           |
| 生命周期管理 | NA                           | -                                                           | __org.apache.dubbo.common.context.Lifecycle__              | NA                                                          |

JDK SPI的实现必须以SPI的类全限定名作为文件名，内容为实现类（或子类）全限定名（可以出现"#"作为注释，也允许有多行）。
实现类（或子类）必须有可访问的无参构造函数用于初始化对象。
JDK SPI非线程安全！

spring并没有完全对标SPI的机制，但有多种类似机制，如实现spring关键接口，并注册到spring上下文，spring会同等对待用户实现。
如早期spring-mvc时代的"META-INF/spring.handlers"，它允许开发者自定义XML的namespace，并自定义解析机制，spring在启动时会查找并调用。
如spring-boot时代的"META-INF/spring.factories"，在该文件以指定注解或接口作为key，类名作为值，spring-boot便会在特定阶段加载并初始化，然后调用他们。
由于spring本身就专注于IoC，开发者很容易实现单例（如通过SpringFactoriesLoader.loadFactoryNames再从ApplicationContext查找/注册）和依赖注入（如EnableAutoConfiguration对应的类，可以使用依赖注入，其定义bean时，参数也是依赖注入的）

Dubbo默认会对所有setter方法进行依赖注入，除非他们是原始类型参数或者有标记DisableInject注解。
ExtensionFactory本身也是SPI实现，可以是dubbo的SPI机制，也可以是spring。
由于接口的SPI注解可声明默认实现，Dubbo SPI机制就不太依赖优先级。
Dubbo默认缓存查找的类以及生成的对象，对于非单例场景则需要先查找类，由开发者再初始化。

ShardingSphere保持JDK SPI的配置方式，在使用SPI的类中，主动注册SPI到ShardingSphereServiceLoader。
在运行时，一旦加载使用SPI的类，静态加载SPI的所有实现类，并进行初始化并缓存。
如果需要为实现类区分优先级，则SPI需继承/实现OrderedSPI接口；如果需要声明默认实现，则SPI需继承/实现RequiredSPI；如果需要过滤，则SPI可以继承/实现TypedSPI。

参考：
1. [micrometer](https://micrometer.io/)
2. [dropwizard metrics](https://metrics.dropwizard.io/)
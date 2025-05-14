# [MicroProfile](https://microprofile.io/)

MicroProfile起初计划包含JAX-RS, CDI, JSON-P几个规范，从6.0版本开始以Jakarta EE 10 Core Profile为基线。

MicroProfile 工作组成员包含IBM、Oracle、Microsoft、RedHat、payara、FUJITSU、Tomitribe、PRIMETON(普元)、AsianInfo(亚信)等公司和部分JUG。

以下是各版本区别：

|                         | MicroProfile 4.1                              | MicroProfile 5.0                                                      | MicroProfile 6.0       | MicroProfile 6.1       |
|:------------------------|:----------------------------------------------|:----------------------------------------------------------------------|:-----------------------|:-----------------------|
| Jakarta EE Core Profile | *CDI 2.0, JSON-P 1.1, JAX-RS 2.1, JSON-B 1.0* | *CDI 3.0, JSON-P 2.0, JAX-RS 3.0, JSON-B 2.0, Jakarta Annotation 2.0* | 10                     | 10                     |
| Config                  | 2.0                                           | 3.0                                                                   | 3.0                    | 3.1                    |
| Fault Tolerance         | 3.0                                           | 4.0                                                                   | 4.0                    | 4.0                    |
| Health                  | 3.1                                           | 4.0                                                                   | 4.0                    | 4.0                    |
| JWT                     | JWT Propagation 1.2                           | JWT Propagation 2.0                                                   | JWT Authentication 2.1 | JWT Authentication 2.1 |
| Metrics                 | 3.0                                           | 4.0                                                                   | 5.0                    | 5.1                    |
| Open API                | 2.0                                           | 3.0                                                                   | 3.1                    | 3.1                    |
| Rest Client             | 2.0                                           | 3.0                                                                   | 3.0                    | 3.0                    |
| Telemetry               | OpenTracing 2.0                               | OpenTracing 3.0                                                       | 1.0                    | 1.1                    |

在Jakarta Config规范发布前，MicroProfile Config是一个不错的选择。
Fault Tolerance、JWT都能很好地补充现有规范体系，而Health和Rest Client可用性不足，Metrics对比Micrometer暂没有明显优势，
Open API设计上使用运行时注解太重，Telemetry则完全是OpenTelemetry的附庸。

MicroProfile主要实现有Open Liberty、Payara、WildFly、~~Apache TomEE~~、[Helidon](https://github.com/helidon-io/helidon)、[fujitsu Launcher](https://github.com/fujitsu/launcher)、 ~~Quarkus~~、~~AISWare FlyingServer~~。

Fujitsu Launcher依赖GlassFish实现Jakarta EE Core Profile，依赖jersey-mp-rest-client和jersey-apache-connector实现Rest Client规范，依赖[smallrye](https://smallrye.io)实现剩余的MicroProfile规范。

[Quarkus](https://quarkus.io)由最开始名称为WildFly Swarm的边车项目，用于创建一个独立的可执行jar，该应用与WildFly应用服务器解耦。 后改名为Thorntail，再更名为Quarkus。

__MicroProfile API依赖eclipse组织类库，有夹带私货之嫌。__

1. Config

|                                 | MP Config                                                                       | spring-cloud-config                                                               | commons-configuration2                                                            | archaius2                                                           |
|:--------------------------------|:--------------------------------------------------------------------------------|:----------------------------------------------------------------------------------|:----------------------------------------------------------------------------------|:--------------------------------------------------------------------|
| sources                         | file, system, environment                                                       | file, system, environment, servlet parameter, jdbc, git, http, random, cli args   | file, JNDI, jdbc, system, servlet parameter, environment, applet                  | environment, system properties, poll                                |
| source file format              | properties                                                                      | properties, yml, *json*                                                           | yml, properties, json, xml, ini, plist                                            | properties                                                          |
| Config Profile                  | META-INF\microprofile-config{-profile}.properties<br>%{profile}.k.e.y=          | bootstrap.yml, application{-profile}.properties                                   | __NA__                                                                            | __NA__                                                              |
| layers/ordinal                  | system, environment, property file                                              | *org.springframework.core.env.MutablePropertySources*                             | *org.apache.commons.configuration2.CombinedConfiguration*                         | runtime, system, environment, remote, application, library, default |
| import file                     | __NA__                                                                          | spring.config.additional-location=f, spring.config.import=configserver:http:h:p/c | include=f, includeoptional=f                                                      | __NA__                                                              |
| bean binding                    | org.eclipse.microprofile.config.inject.ConfigProperties                         | org.springframework.boot.context.properties.ConfigurationProperties               | org.apache.commons.configuration2.beanutils.BeanHelper.INSTANCE.createBean        | com.netflix.archaius.api.annotations.Configuration                  |
| key prefix                      | org.eclipse.microprofile.config.inject.ConfigProperties(prefix=)                | org.springframework.boot.context.properties.ConfigurationProperties(prefix=)      | *org.apache.commons.configuration2.SubsetConfiguration*                           | com.netflix.archaius.api.annotations.Configuration(prefix=)         |
| property binding                | org.eclipse.microprofile.config.inject.ConfigProperty                           | org.springframework.beans.factory.annotation.Value                                | Configuration.getT                                                                | com.netflix.archaius.api.annotations.PropertyName                   |
| Property Default Value          | org.eclipse.microprofile.config.inject.ConfigProperty(defaultValue=)            | org.springframework.beans.factory.annotation.Value("${k.e.y:def}")                | Configuration.getT(key, def)                                                      | com.netflix.archaius.api.annotations.DefaultValue                   |
| Variable Interpolation          | ${k.e.y}, ${key:def}, ${key${inner}}                                            | ${k.e.y}, ${key:def}                                                              | ${sys:k.e.y}, ${const:fqcn.field}, ${env:k.e.y}, ${expr:fqcn.fn(param)}, ${k.e.y} | ${k.e.y}, ${key:def}                                                |
| refer                           | k.e.y=${r.e.f}${e.r.e.n.c.e}                                                    | k.e.y=${r.e.f}${e.r.e.n.c.e}                                                      | k.e.y=${r.e.f}${e.r.e.n.c.e}                                                      | k.e.y=${r.e.f}${e.r.e.n.c.e}                                        |
| Relaxed Binding                 | e_N_V=val --> ${e.n.v}                                                          | camelCase=val --> ${camel-case}                                                   | __NA__                                                                            | __NA__                                                              |
| Data type conversions           | primitive/wrapper, collection/array, T of(S), T valueOf(S), T parse(CS), T(S)   | *org.springframework.core.convert.ConversionService*                              | Configuration.getT                                                                | Config.getT, primitive/wrapper/datetime, collection/map/array, enum |
| Automatic Decode Property Value | __NA__                                                                          | k.e.y=cipher{encoded}                                                             | *org.apache.commons.configuration2.ConfigurationDecoder*                          | *com.netflix.archaius.api.Decoder*                                  |
| refresh                         | __static ConfigSource: microprofile-config.properties,  Environment Variables__ | *org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder*    | runnable: file timestamp check, JMX                                               | runnable: poll                                                      |
| config change listener          | __NA__                                                                          | *org.springframework.context.ApplicationListener*                                 | *org.apache.commons.configuration2.event.EventListener*                           | *com.netflix.archaius.api.ConfigListener*                           |

__MicroProfile Config依赖"biz.aQute.bnd:biz.aQute.bnd.annotation"、"jakarta.enterprise:jakarta.enterprise.cdi-api"、"org.osgi:org.osgi.service.cdi"。__

MicroProfile Config支持通过启动参数"mp.config.profile"来激活指定配置，配置可以在文件名级别或配置项级别含有profile。
MP Config要求system property为dynamic ConfigSource，指定文件和系统环境变量则视为static ConfigSource，而其它配置则由实现规范的供应商决定是否支持动态变更。
对于参数类型为String且为构造函数或函数名为"of"、"valueOf"、"parse"的类型，MicroProfile规范要求自动转换。

SmallRye Config可以自动绑定配置项到Map类型参数，且支持自动解密形如"${aes-gcm-nopadding::encoded}"和"${jasypt::encoded}"的配置项。
在绑定属性前，SmallRye Config可以格局Bean Validation规范，对属性值进行校验。

spring-cloud-config/spring-core支持使用启动参数"spring.profiles.active"或环境变量"SPRING_PROFILES_ACTIVE"来激活指定配置。
spring-cloud-config/spring-boot可以通过参数"spring.config.location"来替换默认的配置文件（即目录为classpath根目录、classpath下config目录、系统当前目录或系统当前目录的config目录及子目录，文件名为的application{-profile}.yml或application{-profile}.properties）。
被ConfigurationProperties注解的bean同普通bean（如有@Value注解的Component）不一样，无需使用RefreshScope注解来刷新配置值，它在ContextRefresher.refresh（通常因RefreshEndpoint启用，被手动调用导致）触发EnvironmentChangeEvent事件后由ConfigurationPropertiesRebinder重新创建bean。

Netflix Archaius 1 基于Apache Commons Configuration 1，而Apache Commons Configuration 1支持对properties文件的value以","分割的值作为List或Array对待，对XML文件的属性使用"node\[@attr]"方式取值。
Archaius 1 支持在配置文件中使用"netflixconfiguration.properties.nextLoad"指定的key（不指定则使用默认的"@next"）来引入其它配置文件。

Netflix Archaius 2与Apache Commons Configuration没有直接关系，但官方提供了Apache Commons Configuration 2 的适配。
Archaius 2对默认值的处理存在缺陷，如"${key:${ref.not.exist}:${ref.exist}}"会保留不存在的"${ref.not.exist}"，而不是解析成空字符串。

2. Fault Tolerance

|                      | MP Fault Tolerance                                     | ~~Netflix Hystrix~~                                               | Resilience4j                                                       | Alibaba Sentinel                                     | spring-cloud                                             |
|:---------------------|:-------------------------------------------------------|:------------------------------------------------------------------|:-------------------------------------------------------------------|:-----------------------------------------------------|:---------------------------------------------------------|
| Asynchronous         | org.eclipse.microprofile.faulttolerance.Asynchronous   | *rxjava*                                                          | __NA__                                                             | _SphU.asyncEntry_                                    | org.springframework.scheduling.annotation.Async          |
| Timeout              | org.eclipse.microprofile.faulttolerance.Timeout        | hystrix.command.default.execution.timeout.enabled                 | io.github.resilience4j.timelimiter.annotation.TimeLimiter          | __NA__                                               | __NA__                                                   |
| Retry                | org.eclipse.microprofile.faulttolerance.Retry          | __NA__                                                            | io.github.resilience4j.retry.annotation.Retry                      | __NA__                                               | org.springframework.retry.annotation.Retryable           |
| : when               | @Retry(retryOn=, abortOn=)                             | -                                                                 | retryOnResultPredicate, retryExceptionPredicate, retryExceptions   | -                                                    | @Retryable(include=, exclude=, exceptionExpression=)     |
| : how                | @Retry(delay=, maxDuration=)                           | -                                                                 | waitDuration, intervalFunction/intervalBiFunction                  | -                                                    | @Retryable(backoff=@Backoff(delay=, multiplier=))        |
| : finally            | @Retry(maxRetries=)                                    | -                                                                 | maxAttempts, failAfterMaxAttempts                                  | -                                                    | @Retryable(maxAttempts=)                                 |
| Fallback             | org.eclipse.microprofile.faulttolerance.Fallback       | com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand    | fallbackMethod=                                                    | fallback=, fallbackClass=                            | __NA__                                                   |
| : when               | @Fallback(applyOn=, skipOn=)                           | *timeout, exception, failure, reject, short-circuits*             | *timeout, exception, failure, reject, short-circuits*              | *exception*                                          | -                                                        |
| : how                | fallbackMethod, FallbackHandler                        | fallbackMethod                                                    | fallbackMethod                                                     | fallbackMethod, fallbackClass                        | -                                                        |
| CircuitBreaker       | org.eclipse.microprofile.faulttolerance.CircuitBreaker | hystrix.command.default.circuitBreaker.enabled                    | io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker    | com.alibaba.csp.sentinel.annotation.SentinelResource | *resilience4j*, *spring-retry CircuitBreakerRetryPolicy* |
| : when               | @CircuitBreaker(failOn=, skipOn=)                      | ignoreExceptions                                                  | recordFailurePredicate, recordExceptions, ignoreExceptionPredicate | exclude BlockedException, exceptionsToIgnore=        | -                                                        |
| : sliding window     | Count-based sliding window                             | Time-based sliding window                                         | Count-based sliding window, Time-based sliding window              | Count-based sliding window                           | -                                                        |
| : threshold          | failure rate                                           | error percentage                                                  | failure rate, slow call rate                                       | SLOW_REQUEST_RATIO, ERROR_RATIO, ERROR_COUNT         | -                                                        |
| : open --> half-open | @CircuitBreaker(delay=)                                | circuitBreaker.sleepWindowInMilliseconds                          | waitDurationInOpenState                                            | statIntervalMs                                       | -                                                        |
| : half-open -->close | @CircuitBreaker(successThreshold=)                     | single test                                                       | permittedNumberOfCallsInHalfOpenState                              | single test                                          | -                                                        |
| Bulkhead             | org.eclipse.microprofile.faulttolerance.Bulkhead       | com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand    | io.github.resilience4j.bulkhead.annotation.Bulkhead                | __NA__                                               | __NA__                                                   |
| : isolation          | semaphores, thread(@Asynchronous)                      | semaphores, thread                                                | semaphores, thread                                                 | -                                                    | -                                                        |
| RateLimiter          | __NA__                                                 | __NA__                                                            | io.github.resilience4j.ratelimiter.annotation.RateLimiter          | QPS(with/without param)/AVG_RT/THREAD_COUNT, cluster | *spring-cloud-gateway request_rate_limiter.lua*          |
| cache                | __NA__                                                 | com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult | Y                                                                  | __NA__                                               | org.springframework.cache.annotation.Cacheable           |
| metrics              | MP Metrics                                             | Y                                                                 | micrometer                                                         | eagleeye                                             | Y                                                        |
| *dynamic config*     | MP Config                                              | Netflix Archaius                                                  | Y                                                                  | nacos/zk/apollo/redis/consul                         | spring-cloud-config                                      |

由于MicroProfile Fault Tolerance规范的API作为Interceptor binding，该规范要求在CDI规范和Interceptor规范兼容环境。
为了设置Interceptor的次序，可以通过配置项"mp.fault.tolerance.interceptor.priority"来改变（默认4010），使得先于/后于指定优先级（即有Priority注解）的Interceptor。
此外，Fault Tolerance的度量信息会按Metrics规范进行统计（暴露在"/metrics/base"路径）；按Config规范进行配置（外部配置覆盖注解配置，方法配置覆盖类级别配置，类级别配置覆盖特性级别配置）。

__MicroProfile Fault Tolerance依赖"jakarta.enterprise:jakarta.enterprise.cdi-api"、"org.osgi:org.osgi.annotation.versioning"。__

Resilience4j的resillience4j-annotation提供了注解支持，但这些注解基本只含有name和fallbackMethod配置，详细配置需通过外部配置或API。

Sentinel以资源为中心，针对资源可以配置各种规则，在方法执行前，根据启用的Slot来处理规则，实现流控(流量整形)、降级、熔断、认证（仅黑白名单）能力，且支持对产生BlockedException指定处理方法。
Sentinel为Servlet、spring-webmvc、spring-cloud-gateway、zuul、dubbo、RocketMQ等主流框架提供了默认适配。
此外，Sentinel提供了一个控制台，可以方便查看、修改规则。

spring-retry的重试策略允许指定间隔时间重试外，还允许后续每次间隔时间成倍数增加。此外，spring-retry提供了Recovery注解，对最大重试次数仍失败后做最终处理。
spring-retry和MP Fault Tolerance一样，都支持每次重试间隔时间随机。

3. Health

|                     | MP Health                                      | spring-boot-actuator                                                          | k8s                           |
|:--------------------|:-----------------------------------------------|:------------------------------------------------------------------------------|:------------------------------|
| health              | *org.eclipse.microprofile.health.HealthCheck*  | _org.springframework.boot.actuate.health.HealthEndpoint_                      | -                             |
| : endpoint          | /health                                        | _/actuator_/health                                                            | _/healthz_                    |
| : payload           | {name:, status: "UP, DOWN, UNKNOWN", data: {}} | {status: "UP, DOWN, UNKNOWN, OUT_OF_SERVICE", components: {}, details: {}}    | 根据HTTP状态码，200~400视为Success    |
| : initial delay     | __NA__                                         | __NA__                                                                        | initialDelaySeconds           |
| : period            | __NA__                                         | __NA__                                                                        | periodSeconds                 |
| : timeout           | __NA__                                         | __NA__                                                                        | timeoutSeconds                |
| : failure threshold | __NA__                                         | __NA__                                                                        | failureThreshold              |
| : success threshold | __NA__                                         | __NA__                                                                        | successThreshold              |
| : terminate wait    | __NA__                                         | __NA__                                                                        | terminationGracePeriodSeconds |
| startup             | org.eclipse.microprofile.health.Startup        | _org.springframework.boot.actuate.startup.StartupEndpoint_                    | -                             |
| : endpoint          | /health/started                                | _/actuator_/health/startup                                                    | -                             |
| readiness           | org.eclipse.microprofile.health.Readiness      | _org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator_ | -                             |
| : endpoint          | /health/ready                                  | _/actuator_/health/readiness, /readyz                                         | _/readyz_                     |
| liveness            | org.eclipse.microprofile.health.Liveness       | _org.springframework.boot.actuate.availability.LivenessStateHealthIndicator_  | -                             |
| : endpoint          | /health/live                                   | _/actuator_/health/liveness, /livez                                           | _/livez_                      |

__MicroProfile Health依赖"biz.aQute.bnd:biz.aQute.bnd.annotation"、"jakarta.enterprise:jakarta.enterprise.cdi-api"、"org.osgi:org.osgi.service.cdi"。__

spring-boot-actuator要求使用参数"management.endpoint.health.probes.enabled=true"来启用探针，通过"management.endpoint.health.probes.add-additional-paths=true"增加k8s兼容的端点。
_startup端点在spring-boot默认禁用，需要编程式开启。_

Kubernetes提供了多种类型探针：shell命令(exec)、TCP(tcpSocket)、HTTP(httpGet)和gRPC(grpc)。
使用HTTP方式探活时并没有默认路径，但Kubernets自身对外暴露的HTTP健康检查端点为"/healthz"，就绪端点为"/readyz"，存活端点为"/livez"。
对于某个特定的资源，可以使用"/livez/resourceName"形式探活。

4. Metrics

|                          | MP Metrics                                          | Micrometer                                          | ~~Netflix Servo~~                                                  |
|:-------------------------|:----------------------------------------------------|:----------------------------------------------------|:-------------------------------------------------------------------|
| REST endpoint            | /metrics                                            | __NA__                                              | __NA__                                                             |
| exposition format        | Prometheus/OpenMetrics                              | text, Prometheus/OpenMetrics                        | -                                                                  |
| context                  | base, vendor, application                           | -                                                   | -                                                                  |
| tags                     | \[a-zA-Z_]\[a-zA-Z0-9_]*                            | @Counted(extraTags=), @Timed(extraTags=)            | com.netflix.servo.annotations.MonitorTags                          |
| metadata                 | name, unit, description                             | -                                                   | -                                                                  |
| metrics: Gauge           | org.eclipse.microprofile.metrics.annotation.Gauge   | *io.micrometer.core.instrument.Gauge*               | com.netflix.servo.annotations.Monitor(type=DataSourceType.GAUGE)   |
| metrics: Counter         | org.eclipse.microprofile.metrics.annotation.Counted | io.micrometer.core.annotation.Counted               | com.netflix.servo.annotations.Monitor(type=DataSourceType.COUNTER) |
| metrics: Histogram       | *org.eclipse.microprofile.metrics.Histogram*        | *io.micrometer.core.instrument.DistributionSummary* | -                                                                  |
| metrics: Timer           | org.eclipse.microprofile.metrics.annotation.Timer   | io.micrometer.core.annotation.Timed                 | *com.netflix.servo.monitor.Timer*                                  |
| publish                  | _Micrometer, OpenTelemetry Metrics, etc._           | JMX, logging, push, prometheus                      | JMX, memory, file                                                  |

__MicroProfile Metrics依赖"jakarta.platform:jakarta.jakartaee-core-api"、"org.osgi:org.osgi.annotation.versioning"。__
MicroProfile Metrics提供了org.eclipse.microprofile.metrics.annotation.Metric用于配合依赖注入或注册到容器，而RegistryScope注解或RegistryType注解用于注入限定的MetricRegistry。
可以通过配置项"mp.metrics.{backend}.enabled"来启用/禁用MicroMeter的后端，为区分应用服务器下多个应用，可以通过配置项"mp.metrics.appName"指定tag "mp_app"的值 。

MicroMeter支持长时间任务（LongTaskTimer），且可以为计时器设置多个百分位，如p99、p95。
MicroMeter的指标数据可以兼容多种后端数据库，如Netflix Atlas、DataDog、Elastic、Graphite、Influx/Telegraf、Prometheus、Wavefront等。

OpenTelemetry的Metrics规范定义了六种类型：Counter（计数器，接收增量来工作，如请求数）、Asynchronous Counter（通过回调来操作）、UpDownCounter（记录变化，当前值）、Asynchronous UpDownCounter、Gauge（异步，当前值）、Histogram（客户端聚合统计，如平均请求耗时）。

Prometheus定义了四种类型：Counter、Gauge、Histogram（包含_sum和_count，需根据值的预期范围和所需精度配置bucket大小，产生较多序列）、Summary（包含_sum和_count，需配置分位数和滑动窗口，产生较少序列）。

5. JWT

__MP JWT依赖"jakarta.enterprise:jakarta.enterprise.cdi-api"、"jakarta.json:jakarta.json-api"、"org.osgi:org.osgi.annotation.versioning"。__

微服务常用的认证方式有[OAuth](https://datatracker.ietf.org/doc/html/rfc6749) 、[OpenID Connect](https://openid.net/developers/how-connect-works/) 、[JWT](https://datatracker.ietf.org/doc/html/rfc7519) 。

MicroProfile JWT Auth可以作为JSR 375 Security规范的一个补充：LoginConfig提供类似web.xml中login-config元素能力；upn和groups信息可以映射成Security规范的IdentityStore。

MP-JWT认证头信息必须包含alg（加密算法，如RS256、ES256）、enc（凭证加密算法，如A256GCM），推荐包含typ（令牌标识，值应该为"JWT"）、kid（JWT加密的key）；
MP-JWT认证体信息必须包含iss（令牌颁发人标识）、iat（JWT颁发时间）、exp（过期时间）、upn（用户主体名称）；推荐包含sub（JWT主体）、jti（JWT唯一标识）、aud（可接受JWT的标识）。

JWS(JSON Web Signature)由三部分组成：JOSE Header、JWS Payload、 JWS Signature。三个部分分别使用base64编码，然后使用"."作为分隔连接符。

JWE(JSON Web Encryption)是一种加密和数据完整性的数据结构，它由JOSE Header、JWE Encrypted Key、JWE Initialization Vector、JWE Ciphertext、JWE Authentication Tag几部分，各部分先经过base64编码，再用"."拼接而成。

6. Open API

|                                       | MP OpenAPI                                                                                                                                                 | ~~springfox~~/Swagger                                                                                                        |
|:--------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------|
| REST annotation                       | JAX-RS, MP OpenAPI                                                                                                                                         | swagger-annotations, spring-web/spring-webflux                                                                               |
| additional annotation                 | Bean Validation                                                                                                                                            | Bean Validation                                                                                                              |
| endpoint                              | /openapi                                                                                                                                                   | ~~/api-docs~~, /v2/api-docs                                                                                                  |
| ui                                    | /openapi/ui                                                                                                                                                | /swagger-ui.html                                                                                                             |
| generate server stubs and client SDKs | __NA__                                                                                                                                                     | *swagger-codegen*                                                                                                            |
| annotations: root document object     | org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition                                                                                             | io.swagger.v3.oas.annotations.OpenAPIDefinition                                                                              |
| annotations: info                     | org.eclipse.microprofile.openapi.annotations.info.Info                                                                                                     | io.swagger.v3.oas.annotations.info.Info                                                                                      |
| annotations: contact info             | org.eclipse.microprofile.openapi.annotations.info.Contact                                                                                                  | io.swagger.v3.oas.annotations.info.Contact                                                                                   |
| annotations: license info             | org.eclipse.microprofile.openapi.annotations.info.License                                                                                                  | io.swagger.v3.oas.annotations.info.License                                                                                   |
| annotations: extensions               | org.eclipse.microprofile.openapi.annotations.extensions.Extension                                                                                          | io.swagger.v3.oas.annotations.extensions.Extension                                                                           |
| annotations: external doc             | org.eclipse.microprofile.openapi.annotations.ExternalDocumentation                                                                                         | io.swagger.v3.oas.annotations.ExternalDocumentation                                                                          |
| annotations: servers                  | org.eclipse.microprofile.openapi.annotations.servers.Server                                                                                                | io.swagger.v3.oas.annotations.servers.Server                                                                                 |
| annotations: server variable          | org.eclipse.microprofile.openapi.annotations.servers.ServerVariable                                                                                        | io.swagger.v3.oas.annotations.servers.ServerVariable                                                                         |
| annotations: tags                     | org.eclipse.microprofile.openapi.annotations.tags.Tag                                                                                                      | io.swagger.v3.oas.annotations.tags.Tag                                                                                       |
| annotations: security                 | org.eclipse.microprofile.openapi.annotations.security.SecurityRequirementsSet<br>org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement | io.swagger.v3.oas.annotations.security.SecurityRequirementsSet<br>io.swagger.v3.oas.annotations.security.SecurityRequirement |
| annotations: components               | org.eclipse.microprofile.openapi.annotations.Components                                                                                                    | __NA__                                                                                                                       |
| annotations: operation                | org.eclipse.microprofile.openapi.annotations.Operation                                                                                                     | io.swagger.v3.oas.annotations.Operation                                                                                      |
| annotations: - parameter              | org.eclipse.microprofile.openapi.annotations.parameters.Parameter                                                                                          | io.swagger.v3.oas.annotations.Parameter                                                                                      |
| annotations: - request header         | org.eclipse.microprofile.openapi.annotations.headers.Header                                                                                                | io.swagger.v3.oas.annotations.headers.Header                                                                                 |
| annotations: - request body           | org.eclipse.microprofile.openapi.annotations.parameters.RequestBody<br>org.eclipse.microprofile.openapi.annotations.parameters.RequestBodySchema           | io.swagger.v3.oas.annotations.parameters.RequestBody                                                                         |
| annotations: - content                | org.eclipse.microprofile.openapi.annotations.media.Content                                                                                                 | io.swagger.v3.oas.annotations.media.Content                                                                                  |
| annotations: - content encoding       | org.eclipse.microprofile.openapi.annotations.media.Encoding                                                                                                | io.swagger.v3.oas.annotations.media.Encoding                                                                                 |
| annotations: - auth                   | org.eclipse.microprofile.openapi.annotations.security.SecurityScheme                                                                                       | io.swagger.v3.oas.annotations.security.SecurityScheme                                                                        |
| annotations: - oauth                  | org.eclipse.microprofile.openapi.annotations.security.OAuthFlow<br>org.eclipse.microprofile.openapi.annotations.security.OAuthScope                        | io.swagger.v3.oas.annotations.security.OAuthFlow<br>io.swagger.v3.oas.annotations.security.OAuthScope                        |
| annotations: - callback url           | org.eclipse.microprofile.openapi.annotations.callbacks.Callback                                                                                            | io.swagger.v3.oas.annotations.callbacks.Callback                                                                             |
| annotations: - callback operation     | org.eclipse.microprofile.openapi.annotations.callbacks.CallbackOperation                                                                                   | io.swagger.v3.oas.annotations.WebHook                                                                                        |
| annotations: - response               | org.eclipse.microprofile.openapi.annotations.responses.APIResponse<br>org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema             | io.swagger.v3.oas.annotations.responses.APIResponse                                                                          |
| annotations: - example                | org.eclipse.microprofile.openapi.annotations.media.ExampleObject                                                                                           | io.swagger.v3.oas.annotations.media.ExampleObject                                                                            |
| annotations: schema                   | org.eclipse.microprofile.openapi.annotations.media.Schema                                                                                                  | io.swagger.v3.oas.annotations.media.Schema                                                                                   |
| annotations: schema property          | org.eclipse.microprofile.openapi.annotations.media.SchemaProperty                                                                                          | io.swagger.v3.oas.annotations.media.SchemaProperty                                                                           |
| annotations: discriminator mapping    | org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping                                                                                    | io.swagger.v3.oas.annotations.media.DiscriminatorMapping                                                                     |
| annotations:  link                    | org.eclipse.microprofile.openapi.annotations.links.Link                                                                                                    | io.swagger.v3.oas.annotations.links.Link                                                                                     |
| annotations:  link parameter          | org.eclipse.microprofile.openapi.annotations.links.LinkParameter                                                                                           | io.swagger.v3.oas.annotations.links.LinkParameter                                                                            |

__MicroProfile Health依赖"biz.aQute.bnd:biz.aQute.bnd.annotation"。__

MicroProfile Open API规范为Open API v3规范提供一个统一的Java API，即开发者既可以编程式构建出OpenAPI文档，也可以使用注解（包括含基本信息的JAX-RS注解和额外信息的MP OpenAPI注解）让供应商扫描后自动生成OpenAPI文档。
开发者也可以在"META-INF/openapi.yml"或"META-INF/openapi.json"存在的情况下，通过配置"mp.openapi.scan.disable=true"来禁用扫描。（*即实现 MP Open API规范的供应商必须实现 MP Config。*）

springfox采用webjar方式将swagger页面引入，支持配置项"springfox.documentation.swagger.v2.path"修改默认的endpoint。
springfox-javadoc项目则可以直接从源代码中解析出OpenAPI文档，但已许久未更新。

运行时保留的注解对开发者和类加载都是负担，也容易暴露敏感信息而被攻击。
[spring-restdocs](https://spring.io/projects/spring-restdocs/) 则要求开发者编写测试用例，通过执行测试用例来生成curl或httpie示例文档。

7. REST Client

REST Client API的主要实现有Apache CXF、Open Liberty、Thorntail、RESTEasy、Jersey。

|                     | MP Rest Client                                                        | Spring Cloud OpenFeign                               |
|:--------------------|:----------------------------------------------------------------------|:-----------------------------------------------------|
| async               | *method return type: CompletionStage*                                 | *method return type: CompletableFuture*              |
| mark                | org.eclipse.microprofile.rest.client.inject.RegisterRestClient        | org.springframework.cloud.openfeign.FeignClient      |
| : name              | @RegisterRestClient(configKey="c.l.a.s.s")                            | @FeignClient(name=)                                  |
| : url               | {c.l.a.s.s}/mp-rest/url=                                              | spring.cloud.openfeign.client.{name}.url=            |
| : connect timeout   | {c.l.a.s.s}/mp-rest/connectTimeout=                                   | spring.cloud.openfeign.client.{name}.connectTimeout= |
| : read timeout      | {c.l.a.s.s}/mp-rest/readTimeout=                                      | spring.cloud.openfeign.client.{name}.readTimeout=    |
| contract            | JAX-RS                                                                | JAX-RS, OpenFeign, spring-web, SOAP                  |
| inject              | @Inject @org.eclipse.microprofile.rest.client.inject.RestClient       | @Resource or @Autowired                              |
| header propagation  | org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders | _feign.RequestInterceptor_                           |
| service discovery   | __NA__                                                                | *Netflix Eureka*                                     |
| fault tolerance     | *MP Fault Tolerance*                                                  | *Netflix Hystrix or Resilience4j*                    |
| client load balance | __NA__                                                                | *Netflix Ribbon or spring-cloud-loadbalancer*        |

MP Rest Client规范可以允许开发者在接口上使用JAX-RS规范定义的注解，来标记请求方法、路径、请求体等信息。
然而，客户端和服务端的签名并不完全相同，比如认证用的token通常在Filter被处理，服务端不需要此参数。
客户端可以通过Rest Client提供的org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam注解来避免这一现象，
ClientHeaderParam的值可以固定，也可以使用形如"{opt.fqcn.fn}"方式，引用一个运行时值，但要求方法必须是返回String或String[]类型的接口无参默认方法，或公开静态方法。

MP Rest Client提供了类似jakarta.ws.rs.ext.Provider用途的org.eclipse.microprofile.rest.client.annotation.RegisterProvider，来个性化JAX-RS的FeatureContext。
如注册ClientRequestFilter、WriterInterceptor、MessageBodyReader、ResponseExceptionMapper等。

如果某些请求头需要透明传递给服务端，则可以通过org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders来启用这一特性，
同时按默认实现类要求，设置"org.eclipse.microprofile.rest.client.propagateHeaders"指定要传递的请求头。

MP Rest Client可以与MP Config规范，开发者可以使用形如"{configKey}/mp-rest/{url|uri|scope|connectTimeout|readTimeout|followRedirects|queryAddress|queryParamStyle|providers/c.l.a.s.s/priority}"完成等价RestClientBuilder的能力。
Fault Tolerance规范的注解存在时，MP Rest Client供应商也必须实现规范目标。

![OpenFeign](https://raw.githubusercontent.com/OpenFeign/feign/master/src/docs/overview-mindmap.iuml)
OpenFeign底层的HTTP客户端可以是JDK自身的，也可以是Apache HTTP Client或Apache HC5，或者Google HTTP、OkHttp。
由于项目源于Netflix，自然可以和Eureka（将clientName解析成真实域名或IP）、Hystrix、Ribbon很好协作。
对于请求体、响应体的编解码则可以是JAXB、Jackson、Gson等工具库。对于度量，则可以与MicroMeter或Dropwizard Metrics集成。


8. Telemetry

Trace一般有三种实现方式：其一为Automatic Instrumentation，即使用框架提供的Filter、Interceptor，或AOP来针对性跟踪标记；
另一为Agent Instrumentation，即使用javaagent方式，在JVM启动时，使用ASM等字节码操作工具，对原有类进行修改；
还有Manual Instrumentation，即允许开发者通过提供的API，来跟踪标记。

|                     | OpenTelemetry                                                                                                                                                | ~~spring-cloud-sleuth~~                                                   |
|:--------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------|
| Instrumentation     | Agent Instrumentation(opentelemetry-java-instrumentation), Manual Instrumentation(opentelemetry-java)                                                        | Automatic Instrumentation                                                 |
| Logs                | LoggerProvider, Logger, Log Record, Log Record Exporter                                                                                                      | __NA__                                                                    |
| : log record        | Timestamp, ObservedTimestamp, TraceId, SpanId, TraceFlags, SeverityText, SeverityNumber, Body, Resource, InstrumentationScope, Attributes                    | %X{traceId:}, %X{spanId:}                                                 |
| Metrics             | MeterProvider, Meter, Metric Exporter, Metric Instruments, Aggregation , Views                                                                               | __NA__                                                                    |
| : metric instrument | name, kind, unit, description                                                                                                                                | -                                                                         |
| : kind              | Counter, Asynchronous Counter, UpDownCounter, Asynchronous UpDownCounter, Gauge, Histogramn                                                                  | -                                                                         |
| Traces              | TracerProvider, Tracer, Trace Exporter, Context Propagation, Span                                                                                            | trace, span                                                               |
| : span              | name, parent span id, start and end timestamps, context, attributes, events, links, status                                                                   | org.springframework.cloud.sleuth.SpanName                                 |
| : span context      | trace id, span id, trace flag, trace state                                                                                                                   | traceId, spanId, parentSpanId, sampled                                    |
| : span status       | UNSET, OK, ERROR                                                                                                                                             | __NA__                                                                    |
| : span kind         | INTERNAL, SERVER, CLIENT, PRODUCER, CONSUMER                                                                                                                 | SERVER, CLIENT, PRODUCER, CONSUMER                                        |
| new span            | io.opentelemetry.instrumentation.annotations.WithSpan                                                                                                        | org.springframework.cloud.sleuth.annotation.NewSpan                       |
| span attribute      | io.opentelemetry.instrumentation.annotations.SpanAttribute                                                                                                   | org.springframework.cloud.sleuth.annotation.SpanTag                       |
| sampling            | always_on, always_off, traceidratio, jaeger_remote, parentbased_always_on, parentbased_always_off, parentbased_traceidratio, parentbased_jaeger_remote, xray | spring.sleuth.sampler.rate=                                               |
| Context Propagation | tracecontext(W3C), baggage(W3C), b3, b3multi, jaeger, xray, ottrace, none                                                                                    | spring.sleuth.propagation.type: AWS, W3C, B3                              |
| Baggage             | W3C                                                                                                                                                          | spring.sleuth.baggage.remote-fields=, spring.sleuth.baggage.local-fields= |
| collect             | direct, agent, gateway                                                                                                                                       | Brave                                                                     |
| export              | OTLP, Jaeger, Zipkin, Prometheus, Logging                                                                                                                    | Zipkin                                                                    |

MicroProfile Telemetry采纳Open Telemetry规范提供的Java API，并规定供应商需集成CDI、MP Config、JAX-RS。（但未强制要求Logging、Metrics的集成）

__Baggage和Span Attribute的不同点在于，Baggage用于传递跨越span的上下文信息。__

参考：
1. [MicroProfile Config](https://microprofile.io/specifications/microprofile-config/)
2. [MicroProfile Fault Torelance](https://microprofile.io/specifications/microprofile-fault-tolerance/)
3. [OpenSergo](https://opensergo.io/zh-cn/)
4. [MicroProfile Health](https://microprofile.io/specifications/microprofile-health/)
5. [MicroProfile Metrics](https://microprofile.io/specifications/microprofile-metrics/)
6. [MicroProfile JWT Auth](https://microprofile.io/specifications/microprofile-jwt-auth/)
7. [MicroProfile Open API](https://microprofile.io/specifications/microprofile-open-api/)
8. [OpenAPI Specification](https://spec.openapis.org/oas/latest.html)
9. [MicroProfile REST Client](https://microprofile.io/specifications/microprofile-rest-client/)
10. [MicroProfile Telemetry](https://microprofile.io/specifications/microprofile-telemetry/)
11. [OpenTelemetry](https://opentelemetry.io/)
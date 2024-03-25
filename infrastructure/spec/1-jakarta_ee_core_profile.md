# Jakarta EE Core Profile

JakartaEE目前包含三个Profile，即Core、Web、Platform。三者之间存在包含关系，而每个Profile包含一堆JSR。关系图如下
![JAKARTA EE 10](https://jakarta.ee/release/10/images/jakarta-ee-10-platform.svg)

核心概要文件（Core Profile）是企业版Java规范的一个子集，它的目标群体为微服务开发人员。

必要规范包括 Annotations, DI, CDI Lite, Interceptors, JSONP, JSONB, JAX-RS。

可选规范包括：JAXB（JAX-RS可选依赖）, EL（CDI可选依赖）。

0. Annotations

在JavaEE也被称为common-annotations，它是在JSR 175元注解规范出现后，为避免通用概念因不同厂商的技术实现带来不同的注解。

|         | Annotations                                               | Spring                                                                | 备注                                              |
|:--------|:----------------------------------------------------------|:----------------------------------------------------------------------|:------------------------------------------------|
| 标记为资源   | jakarta.annotation.Resource                               | org.springframework.stereotype.Repository                             | Resource用于指定EJB、WebService、Persistence等资源       |
| 属性/方法注入 | jakarta.annotation.Resource                               | org.springframework.beans.factory.annotation.Autowired                | @Resource默认按名称注入，找不到则按类型，支持从JNDI获取              |
| 顺序      | jakarta.annotation.Priority                               | org.springframework.core.annotation.Order                             | @Priority的负值作为特殊用途                              |
| 构造函数钩子  | jakarta.annotation.PostConstruct                          | *org.springframework.beans.factory.InitializingBean*                  | Spring还支持其他init-method方式                        |
| 析构函数钩子  | jakarta.annotation.PreDestroy                             | *org.springframework.beans.factory.DisposableBean*                    | Spring还支持AutoCloseable等destroy-method方式         |
| 数据源     | jakarta.annotation.sql.DataSourceDefinition               | -                                                                     | Spring可以通过在java配置类定义javax.sql.DataSource类型的bean |
| 权限：定义角色 | jakarta.annotation.security.DeclareRoles                  | -                                                                     | Annotations还定义了其他权限相关注解                         |
| 权限：资源角色 | jakarta.annotation.security.RolesAllowed                  | org.springframework.security.access.annotation.Secured                | spring-security的PreAuthorize注解支持表达式可以达到相同效果     |
| 权限：角色   | jakarta.annotation.security.RunAs                         | -                                                                     | Annotations还定义了其他权限相关注解                         |
| 约束      | jakarta.annotation.Nonnull<br>jakarta.annotation.Nullable | org.springframework.lang.NonNull<br>org.springframework.lang.Nullable | 用于属性上则构造完不能/可能为null，用于方法上则返回值不能/可能为null         |

可以看到Annotations规范定义的注解数量不多，但涉及面挺广，甚至有些注解平常用不到。

1. DI & CDI

DI即Dependency Injection，该规范相较于传统的构造函数方式、工厂模式方式及Service Locator方式，为获取对象提供了一种更好的重用性、可测试性、可维护性方式。
DI和CDI(Context Dependency Injection)的参考实现为JBoss Weld，但glassfish使用的是hk2。

|            | DI & CDI                                                                                                                                                                                                                                                                                                       | spring-context                                                                                                                      | guice                        | 备注                                                             |
|:-----------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------|:---------------------------------------------------------------|
| 配置方式       | annotation, xml                                                                                                                                                                                                                                                                                                | xml, properties, jdbc, java                                                                                                         | java                         |                                                                |
| 忽略配置内的bean | jakarta.enterprise.inject.Vetoed                                                                                                                                                                                                                                                                               | -                                                                                                                                   | -                            | JakartaEE可忽略包/类中的bean，此方式可移植性差                                 |
| 类标记为bean   | jakarta.enterprise.context.NormalScope<br>jakarta.enterprise.inject.Stereotype<br>jakarta.enterprise.context.Dependent                                                                                                                                                                                         | org.springframework.stereotype.Component                                                                                            | -                            | JakartaEE中使用NormalScope、Stereotype元注解的自定义注解标记的类型都被当作bean       |
| 定义bean     | jakarta.enterprise.inject.Produces                                                                                                                                                                                                                                                                             | org.springframework.context.annotation.Bean                                                                                         | com.google.inject.Provides   |                                                                |
| 条件         | jakarta.enterprise.inject.Alternative                                                                                                                                                                                                                                                                          | org.springframework.context.annotation.Conditional                                                                                  | -                            | JakartaEE的Alternative注解使bean默认禁用，需要在beans.xml激活或添加Priority直接启用 |
| scope      | jakarta.inject.Singleton<br>jakarta.enterprise.context.ApplicationScoped<br>jakarta.enterprise.context.SessionScoped<br>jakarta.enterprise.context.RequestScoped                                                                                                                                               | singleton, prototype; session, request, refresh; custom                                                                             | com.google.inject.Singleton  | 可以通过JakartaEE元注解jakarta.inject.Scope自定义scope                   |
| 限定注册到上下文类型 | jakarta.enterprise.inject.Typed                                                                                                                                                                                                                                                                                | -                                                                                                                                   | -                            |                                                                |
| 构造器注入      | jakarta.inject.Inject                                                                                                                                                                                                                                                                                          | org.springframework.beans.factory.annotation.Autowired                                                                              | com.google.inject.Inject     | spring支持Resource、Inject注解                                      |
| 属性注入       | jakarta.inject.Inject                                                                                                                                                                                                                                                                                          | org.springframework.beans.factory.annotation.Autowired                                                                              | com.google.inject.Inject     |                                                                |
| 方法注入       | jakarta.inject.Inject                                                                                                                                                                                                                                                                                          | org.springframework.beans.factory.annotation.Autowired                                                                              | com.google.inject.Inject     |                                                                |
| 默认bean     | jakarta.enterprise.inject.Default                                                                                                                                                                                                                                                                              | org.springframework.context.annotation.Primary                                                                                      | -                            | guice可以通过ImplementedBy来指定默认实现类型                                |
| 限定名称       | jakarta.inject.Named                                                                                                                                                                                                                                                                                           | org.springframework.beans.factory.annotation.Qualifier                                                                              | com.google.inject.name.Named | jakarta.inject.Qualifier在JakartaEE为元注解                         |
| 可选依赖       | *jakarta.inject.Provider*                                                                                                                                                                                                                                                                                      | *java.util.Optional*<br>org.springframework.beans.factory.annotation.Autowired(required=false)                                      | *java.util.Optional*         |                                                                |
| 延迟加载       | *jakarta.inject.Provider*                                                                                                                                                                                                                                                                                      | org.springframework.context.annotation.Lazy                                                                                         | -                            |                                                                |
| 避免循环依赖     | *jakarta.inject.Provider*                                                                                                                                                                                                                                                                                      |                                                                                                                                     | -                            |                                                                |
| 多实例注入      | *jakarta.inject.Provider*                                                                                                                                                                                                                                                                                      | java.util.Collection<br>java.util.Map                                                                                               | -                            |                                                                |
| bean销毁清理   | jakarta.enterprise.inject.Disposes                                                                                                                                                                                                                                                                             | *org.springframework.beans.factory.DisposableBean*                                                                                  | -                            | spring支持对Closeable类型自动发现销毁方法进行调用                               |
| 上下文初始化通知   | jakarta.enterprise.context.Initialized                                                                                                                                                                                                                                                                         | *org.springframework.context.event.ContextStartedEvent*                                                                             | -                            |                                                                |
| 上下文销毁前通知   | jakarta.enterprise.context.BeforeDestroyed                                                                                                                                                                                                                                                                     | -                                                                                                                                   | -                            |                                                                |
| 上下文销毁通知    | jakarta.enterprise.context.Destroyed                                                                                                                                                                                                                                                                           | *org.springframework.context.event.ContextStoppedEvent*                                                                             | -                            |                                                                |
| 产生事件       | *jakarta.enterprise.event.Event*                                                                                                                                                                                                                                                                               | *org.springframework.context.ApplicationEventPublisher*                                                                             | -                            | CDI中通过注入的Event来产生同步/异步事件                                       |
| 事件处理       | jakarta.enterprise.event.Observes<br>jakarta.enterprise.event.ObservesAsync                                                                                                                                                                                                                                    | org.springframework.context.event.EventListener                                                                                     | -                            |                                                                |
| SPI        | jakarta.enterprise.inject.build.compatible.spi.Discovery<br>jakarta.enterprise.inject.build.compatible.spi.Enhancement<br>jakarta.enterprise.inject.build.compatible.spi.Registration<br>jakarta.enterprise.inject.build.compatible.spi.Synthesis<br>jakarta.enterprise.inject.build.compatible.spi.Validation | *org.springframework.beans.factory.config.BeanFactoryPostProcessor*<br>*org.springframework.beans.factory.config.BeanPostProcessor* | -                            |                                                                |

CDI以元注解NormalScope标记常规范围，将DI的元注解Scope留作标记伪范围。CDI给JSF预留了ConversationScoped，此外还定义了一个特殊的伪范围Dependent。
DI和CDI规范没有预定义prototype范围，但CDI有个TransientReference，它在方法执行结束销毁指定bean。

CDI提供了通用元注解Stereotype用于为自定义注解来将范围和拦截器等注解进行结合，比如用于MVC架构模式下JSF使用而内建的：jakarta.enterprise.inject.Model。

CDI允许销毁清理方法通过参数上的Disposes注解注入销毁对象或其它辅助对象。

开发、测试、生产环境需要生效的bean可能不一致（可参考Spring的@Profile，结合元注解@Qualifier自定义注解达到目的），或者值不一致（可参考Spring的@Value、@ConfigurationProperties）。

散落的配置写起来方便，阅读，特别是排查问题就麻烦，需要支持集中式配置（可参考Spring的@Configuration、@Import、@ImportResource）。

在JakartaEE中定义了Decorator类型bean，但他实现了业务逻辑接口类，所以无法像Interceptor那样关注横切点。
Decorator类型bean可以注入业务接口，并用Delegate注解来限定。
Decorator注解CDI Lite可以不支持。

__Resource和Inject都可以用于注入容器管理对象，一般Resource用于注入资源（即外部系统，如JDBC、EJB、JMS、EIS、JavaMail等），它可以使用JNDI名称，但缺乏类型安全。__

2. Interceptor

Interceptors规范定义了拦截器基本编程模型和语义。Interceptor参考实现为JBoss Weld。

|          | Interceptors                            | spring-aop                                     | Eclipse Aspectj                            |
|:---------|:----------------------------------------|:-----------------------------------------------|:-------------------------------------------|
| 织入时机     | runtime                                 | runtime or load time                           | compile                                    |
| 标记为切面    | jakarta.interceptor.Interceptor         | *org.springframework.aop.Advisor*              | org.aspectj.lang.annotation.Aspect         |
| 标记为连接点   | jakarta.interceptor.Interceptors        | -                                              | -                                          |
| 标记为切点    | jakarta.interceptor.InterceptorBinding  | *org.springframework.aop.Pointcut*             | org.aspectj.lang.annotation.Pointcut       |
| 方法执行前增强  | -                                       | *org.springframework.aop.BeforeAdvice*         | org.aspectj.lang.annotation.Before         |
| 方法执行后增强  | -                                       | *org.springframework.aop.AfterAdvice*          | org.aspectj.lang.annotation.After          |
| 方法返回后增强  | -                                       | *org.springframework.aop.AfterReturningAdvice* | org.aspectj.lang.annotation.AfterReturning |
| 方法异常时增强  | -                                       | *org.springframework.aop.ThrowsAdvice*         | org.aspectj.lang.annotation.AfterThrowing  |
| 环绕增强     | jakarta.interceptor.AroundInvoke        | -                                              | org.aspectj.lang.annotation.Around         |

Jakarta EE支持将构造函数作为切点，允许环绕(AroundConstruct)和构造后(PostConstructor)处理。它们与销毁前(PreDestroy)处理，构成生命周期回调。

Jakarta EE在声明切点时，支持直接在业务方法上通过Interceptors注解绑定拦截器。
另一方面，允许通过InterceptorBinding来自定义注解，在业务方法上添加该自定义注解，同时切面上也添加该自定义注解，来达成匹配的目的。

JakartaEE还支持超时回调(AroundTimeout)、支持个别构造函数/方法通过ExcludeClassInterceptors或ExcludeDefaultInterceptors来排除类上指定的通知。

AOP相关的框架、库还有JBoss AOP、AspectWerkz、Nanning等，它们和AOP联盟（aopalliance）一样，基本不再存活/更新。

3. JSONB/JSONP

   JSONB即JSON Binding，用于规范java对象序列化成JSON消息，或JSON消息反序列化成java对象。参考实现为Eclipse Yasson。

|            | JSONB                                                                                   | jackson-json                                                                                                                                                 | gson                                                                       | fastjson                                                                                                                                  |
|:-----------|:----------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------|
| 带参构造函数反序列化 | jakarta.json.bind.annotation.JsonbCreator                                               | com.fasterxml.jackson.annotation.JsonCreator<br>com.fasterxml.jackson.annotation.JsonProperty<br>com.fasterxml.jackson.annotation.JacksonInject              | -                                                                          | com.alibaba.fastjson.annotation.JSONCreator<br>com.alibaba.fastjson.annotation.JSONField                                                  |
| builder模式  | -                                                                                       | com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder                                                                                                    | -                                                                          | com.alibaba.fastjson.annotation.JSONType(builder = )<br>com.alibaba.fastjson.annotation.JSONPOJOBuilder                                   |
| 指定属性序列化时顺序 | jakarta.json.bind.annotation.JsonbPropertyOrder                                         | com.fasterxml.jackson.annotation.JsonPropertyOrder                                                                                                           | -                                                                          | com.alibaba.fastjson.annotation.JSONType(orders = )<br>com.alibaba.fastjson.annotation.JSONField(ordinal = )                              |
| 属性别名       | jakarta.json.bind.annotation.JsonbProperty                                              | com.fasterxml.jackson.annotation.JsonProperty<br>com.fasterxml.jackson.annotation.JsonAlias                                                                  | com.google.gson.annotations.SerializedName                                 | com.alibaba.fastjson.annotation.JSONField(alternateNames = )                                                                              |
| 忽略属性       | jakarta.json.bind.annotation.JsonbTransient                                             | com.fasterxml.jackson.annotation.JsonIgnore<br>com.fasterxml.jackson.annotation.JsonIgnoreProperties<br>com.fasterxml.jackson.annotation.JsonIgnoreType      | com.google.gson.annotations.Expose(serialize = false, deserialize = false) | com.alibaba.fastjson.annotation.JSONType(ignores = )<br>com.alibaba.fastjson.annotation.JSONField(serialize = false, deserialize = false) |
| 指定日期格式化方式  | jakarta.json.bind.annotation.JsonbDateFormat                                            | com.fasterxml.jackson.annotation.JsonFormat                                                                                                                  | *com.google.gson.GsonBuilder#setDateFormat*                                | com.alibaba.fastjson.annotation.JSONField(format = )                                                                                      |
| 指定数字格式化方式  | jakarta.json.bind.annotation.JsonbNumberFormat                                          | com.fasterxml.jackson.annotation.JsonFormat                                                                                                                  | -                                                                          | com.alibaba.fastjson.annotation.JSONField(format = )                                                                                      |
| 泛型         | -                                                                                       | *com.fasterxml.jackson.core.type.TypeReference*                                                                                                              | *com.google.gson.reflect.TypeToken*                                        | com.alibaba.fastjson.TypeReference                                                                                                        |
| 多态         | jakarta.json.bind.annotation.JsonbTypeInfo<br>jakarta.json.bind.annotation.JsonbSubtype | com.fasterxml.jackson.annotation.JsonTypeInfo<br>com.fasterxml.jackson.annotation.JsonSubTypes<br>com.fasterxml.jackson.databind.annotation.JsonTypeResolver | -                                                                          | com.alibaba.fastjson.annotation.JSONType(seeAlso = {}, typeKey = )                                                                        |
| 自定义反序列化    | jakarta.json.bind.annotation.JsonbTypeDeserializer                                      | com.fasterxml.jackson.databind.annotation.JsonDeserialize                                                                                                    | *com.google.gson.JsonDeserializer*                                         | com.alibaba.fastjson.annotation.JSONType(deserializer = )                                                                                 |
| 自定义序列化     | jakarta.json.bind.annotation.JsonbTypeSerializer                                        | com.fasterxml.jackson.databind.annotation.JsonSerialize                                                                                                      | *com.google.gson.JsonSerializer*                                           | com.alibaba.fastjson.annotation.JSONType(serializer = )                                                                                   |
| 包/类属性可见性   | jakarta.json.bind.annotation.JsonbVisibility                                            | -                                                                                                                                                            | -                                                                          | -                                                                                                                                         |
| 中间对象转换     | jakarta.json.bind.annotation.JsonbTypeAdapter                                           | -                                                                                                                                                            | com.google.gson.annotations.JsonAdapter                                    | -                                                                                                                                         |
| 属性名映射策略    | *jakarta.json.bind.config.PropertyNamingStrategy*                                       | com.fasterxml.jackson.databind.annotation.JsonNaming<br>*com.fasterxml.jackson.databind.PropertyNamingStrategy*                                              | *com.google.gson.FieldNamingStrategy*                                      | com.alibaba.fastjson.annotation.JSONType(naming = )<br>*com.alibaba.fastjson.PropertyNamingStrategy*                                      |
| mixin      | -                                                                                       | org.springframework.boot.jackson.JsonMixin<br>com.fasterxml.jackson.annotation.JsonAnyGetter<br>com.fasterxml.jackson.annotation.JsonAnySetter               | -                                                                          | *com.alibaba.fastjson.JSON.addMixInAnnotations(,)*                                                                                        |
| 注入上下文对象    | -                                                                                       | com.fasterxml.jackson.annotation.JacksonInject                                                                                                               | -                                                                          | -                                                                                                                                         |
| 引用         | -                                                                                       | com.fasterxml.jackson.annotation.JsonIdentityInfo<br>com.fasterxml.jackson.annotation.JsonIdentityReference                                                  | -                                                                          | __jsonpath__                                                                                                                              |

JSONP即JSON Processing，用于规范JSON文档的解析、生成、修改及查询。参考实现为Eclipse Parsson。

|        | JSONP                               | jackson-json                               | guava                              | fastjson                                         |
|:-------|:------------------------------------|:-------------------------------------------|:-----------------------------------|:-------------------------------------------------|
| 解析JSON | *jakarta.json.stream.JsonParser*    | *com.fasterxml.jackson.core.JsonParser*    | com.google.gson.stream.JsonReader  | *com.alibaba.fastjson.parser.DefaultJSONParser*  |
| 生成JSON | *jakarta.json.stream.JsonGenerator* | *com.fasterxml.jackson.core.JsonGenerator* | com.google.gson.stream.JsonWriter  | *com.alibaba.fastjson.serializer.JSONSerializer* |

从API来看，JSONB、JSONP与jackson极其相似。

gson虽然没有注解来支持带参构造函数，但是gson使用类似objenesis方式(objenesis还支持ReflectionFactory等手段)，即通过Unsafe、ObjectInputStream或ObjectStreamClass等手段来生成对象。

4. JAX-RS & MVC

JAX-RS即Java RESTful Web Services。参考实现为Eclipse Jersey（前身为Sun Jersey），其它实现有JBoss RESTEasy和Apache CXF。
MVC规范基于JAX-RS，所以此处将二者当作一个整体和其它规范/库进行比较。MVC规范的参考实现为Eclipse Krazo。


|              | JAX-RS & MVC                                         | spring-webmvc                                                               | 备注                                                                                  |
|:-------------|:-----------------------------------------------------|:----------------------------------------------------------------------------|:------------------------------------------------------------------------------------|
| 控制器          | jakarta.mvc.Controller                               | org.springframework.stereotype.Controller                                   | spring-webmvc的Controller注解支持命名bean                                                  |
| URL映射        | jakarta.ws.rs.Path                                   | org.springframework.web.bind.annotation.RequestMapping                      | spring-webmvc的RequestMapping可以同时指定method, params, headers, consumes, produces       |
| 限定为GET方法     | jakarta.ws.rs.GET                                    | org.springframework.web.bind.annotation.GetMapping                          | 同上                                                                                  |
| 限定为POST方法    | jakarta.ws.rs.POST                                   | org.springframework.web.bind.annotation.DeleteMapping                       | 同上                                                                                  |
| 限定为PUT方法     | jakarta.ws.rs.PUT                                    | org.springframework.web.bind.annotation.PutMapping                          | 同上                                                                                  |
| 限定为DELETE方法  | jakarta.ws.rs.DELETE                                 | org.springframework.web.bind.annotation.DeleteMapping                       | 同上                                                                                  |
| 限定为PATCH方法   | jakarta.ws.rs.PATCH                                  | org.springframework.web.bind.annotation.PatchMapping                        | 同上。注意：非幂等，对资源进行部分修改                                                                 |
| 限定为OPTIONS方法 | jakarta.ws.rs.OPTIONS                                | -                                                                           | spring-webmvc在HandlerMapping中识别预检请求，根据跨域配置决定放行还是拒绝                                  |
| 限定为HEAD方法    | jakarta.ws.rs.HEAD                                   | -                                                                           | 获取文件大小用。spring-webmvc无对应方式，可通过GetMapping变通实现                                        |
| 限定为TRACE方法   | -                                                    | -                                                                           |                                                                                     |
| 限定消费的媒体类型    | jakarta.ws.rs.Consumes                               | org.springframework.web.bind.annotation.RequestMapping(consumes = )         |                                                                                     |
| 限定产生的媒体类型    | jakarta.ws.rs.Produces                               | org.springframework.web.bind.annotation.RequestMapping(produces = )         |                                                                                     |
| 绑定请求头        | jakarta.ws.rs.HeaderParam                            | org.springframework.web.bind.annotation.RequestHeader                       | Jakarta MVC的绑定参数如果是复杂对象，需要实现ParamConverterProvider并注册到容器                            |
| 绑定Cookie     | jakarta.ws.rs.CookieParam                            | org.springframework.web.bind.annotation.CookieValue                         | 同上                                                                                  |
| 绑定session    | -                                                    | org.springframework.web.bind.annotation.SessionAttribute                    |                                                                                     |
| 绑定简单查询参数     | jakarta.ws.rs.QueryParam                             | org.springframework.web.bind.annotation.RequestParam                        |                                                                                     |
| 绑定矩阵查询参数     | jakarta.ws.rs.MatrixParam                            | org.springframework.web.bind.annotation.MatrixVariable                      |                                                                                     |
| 绑定路径模板参数     | jakarta.ws.rs.PathParam                              | org.springframework.web.bind.annotation.PathVariable                        |                                                                                     |
| 绑定表单参数       | jakarta.ws.rs.FormParam                              | -                                                                           | MIME类型需为"application/x-www-form-urlencoded"。Spring MVC无需注解，使用java bean会自动绑定         |
| 绑定到对象        | jakarta.ws.rs.BeanParam                              | -                                                                           | 对象属性需使用JAX-RS注解                                                                     |
| 绑定请求体        | __java.lang.Object__                                 | org.springframework.web.bind.annotation.RequestBody                         | Jakarta MVC支持将RequestScoped的bean作为属性注入Controller                                    |
| 绑定上传的文件      | -                                                    | org.springframework.web.bind.annotation.RequestPart                         |                                                                                     |
| 绑定请求属性       | -                                                    | org.springframework.web.bind.annotation.RequestAttribute                    |                                                                                     |
| 设置默认值        | jakarta.ws.rs.DefaultValue                           | org.springframework.web.bind.annotation.*(defaultValue = )                  | spring-webmvc的RequestHeader、CookieValue、RequestParam、MatrixVariable支持默认值            |
| 返回视图         | *java.lang.String*<br>jakarta.mvc.View               | *java.lang.String<br>org.springframework.web.servlet.View*                  | Jakarta MVC的@View注解用于返回固定的默认视图                                                      |
| 返回模型和视图      | *jakarta.mvc.Models*                                 | *org.springframework.web.servlet.ModelAndView*                              |                                                                                     |
| 返回响应码        | *jakarta.ws.rs.core.Response*                        | org.springframework.web.bind.annotation.ResponseStatus                      |                                                                                     |
| 返回响应体        | *jakarta.ws.rs.core.Response*                        | org.springframework.web.bind.annotation.ResponseBody                        | JakartaEE通过在实现了MessageBodyWriter和MessageBodyReader的类上标记@Provider来自动编解码              |
| 全局异常处理       | *jakarta.ws.rs.ext.ExceptionMapper*                  | org.springframework.web.bind.annotation.ExceptionHandler                    | JakartaEE需要用@Provider注解标记实现类，将其注册到容器                                                |
| 编码           | *jakarta.ws.rs.ext.MessageBodyWriter*                | *org.springframework.http.codec.HttpMessageWriter*                          |                                                                                     |
| 解码           | *jakarta.ws.rs.ext.MessageBodyReader*                | *org.springframework.http.codec.HttpMessageReader*                          | @Encoded仅用于标记无需解码                                                                   |
| 参数转换         | *jakarta.ws.rs.ext.ParamConverter*                   | *org.springframework.http.converter.HttpMessageConverter*                   |                                                                                     |
| 参数校验         | jakarta.validation.Valid                             | org.springframework.validation.annotation.Validated                         | spring-webmvc也支持规范的注解，但自定义注解支持分组校验；Jakarta MVC支持用MvcBinding，将违规约束放到注入的BindingResult |
| 跨域           | jakarta.mvc.security.CsrfProtected                   | org.springframework.web.bind.annotation.CrossOrigin                         |                                                                                     |
| 本地化          | *jakarta.mvc.locale.LocaleResolver*                  | *org.springframework.web.servlet.LocaleResolver*                            | Jakarta MVC支持注入MvcContext来过程控制                                                      |
| 异步           | jakarta.ws.rs.container.Suspended                    | org.springframework.scheduling.annotation.Async                             |                                                                                     |
| SSE          | jakarta.ws.rs.Produces(MediaType.SERVER_SENT_EVENTS) | *org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter* |                                                                                     |

JAX-RS提供了一个类似servlet中context-path概念的注解：jakarta.ws.rs.ApplicationPath。
为区分bean是给服务端使用，还是客户都安使用，可以通过ConstrainedTo来限定。
为绑定过滤器，可以通过NameBinding元注解来自定义注解，将RESTful资源方法与自定义过滤进行绑定。

JAX-RS既可以在JavaSE环境使用(支持JAX-WS发布的实现还需支持jakarta.xml.ws.Provider类型的Endpoint)，也可以在Servlet容器使用。

| 环境      | 名称                        | 方式                                                                      | 要求                                                              | 备注        |
|:--------|:--------------------------|:------------------------------------------------------------------------|:----------------------------------------------------------------|:----------|
| JavaSE  | Java SE Endpoint          | jakarta.ws.rs.ext.RuntimeDelegate.createEndpoint                        | 存在RuntimeDelegate子类<br>存在Application子类                          |           |
| JavaSE  | Java SE Bootstrap         | jakarta.ws.rs.SeBootstrap.start(Application, Configuration)             | Java SE Endpoint的要求<br>存在SeBootstrap的SPI实现<br>存在Configuration实现 | 推荐方式（可移植） |
| Servlet | -不存在Application子类         | web.xml配置servlet-name为jakarta.ws.rs.core.Application，并配置servlet-mapping | 程序动态注册一个servlet，名字必须是jakarta.ws.rs.core.Application             |           |
| Servlet | -Application子类已被Servlet处理 | 无需额外处理                                                                  | servlet的init-param为jakarta.ws.rs.Application子类                  |           |
| Servlet | -Application子类未被Servlet处理 | 程序动态注册一个servlet，servlet-name为Application子类全限定名                          | Application子类存在ApplicationPath注解，或者在web.xml添加servlet-mapping    |           |

在JAX-RS这种RESTful WebService规范出现之前，还有SOAP WebService规范，即JAX-RPC(Java API for XML-based RPC)和JAX-WS(Java API for XML-Based Web Services)。

JAX-RPC要求SEI(Service Endpoint Interface)必须继承java.rmi.Remote，方法必须抛出java.rmi.RemoteException。
而client可以从SEI或WSDL生成java代码，或使用java.xml.rpc.ServiceFactory.loadService来获取stub，使用起来相当不便。

JAX-WS在JAX-RPC基础上进行了扩展，则定义了一系列注解(这些注解属于Java Web Services Metadata)，包括用于标记类为SEI的jakarta.jws.WebService，标记方法的jakarta.jws.WebMethod，标记参数的jakarta.jws.WebParam，
标记结果的jakarta.jws.WebResult和jakarta.jws.Oneway，引入外部定义处理链的jakarta.jws.HandlerChain。JAX-WS也支持SOAP绑定。

JAX-RPC要事先生成stub，而JAX-WS允许在运行时生成stub。
JAX-WS曾被纳入JavaSE。

Enterprise Web Services要求产商必须至少实现其一：

+ 在Servlet容器实现Jakarta XML Web Services
+ 以无状态session bean或单例bean实现Jakarta XML Web Services

5. JAXB & JAXP

JAXB即Java Architecture for XML Binding，它是JAX-WS的默认数据绑定技术, JAXB的参考实现为jaxb-impl。

JAXB架构上由schema编译器、schema生成器和绑定运行时框架组成。

schema编译器(Schema Compiler)使用基于XML的语言来描述绑定关系，schema编译将源schema编译出衍生的程序元素。

schema生成器(Schema Generator)使用程序注解来映射已有的程序元素和衍生的schema。

绑定运行时框架(binding runtime framework)提供编组(Marshalling和解组(Unmarshalling)这两种基本的操作，来访问、操控和校验XML内容。

JAXB使用java注解来标记java对象和XML之间的绑定关系，XStream和jackson-dataformat-xml也支持注解形式，此处就不再比较编写XML映射关系来生成代码形式的XMLBeans和JiBX。

|                | JAXB                                                                                              | XStream                                                 | jackson-dataformat-xml                                                               | 备注                                           |
|:---------------|:--------------------------------------------------------------------------------------------------|:--------------------------------------------------------|:-------------------------------------------------------------------------------------|:---------------------------------------------|
| 绑定attribute    | jakarta.xml.bind.annotation.XmlAttribute                                                          | com.thoughtworks.xstream.annotations.XStreamAsAttribute | com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(isAttribute=true) |                                              |
| 绑定元素           | jakarta.xml.bind.annotation.XmlElement                                                            | com.thoughtworks.xstream.annotations.XStreamAlias       | com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty                   |                                              |
| 绑定通配attribute  | jakarta.xml.bind.annotation.XmlAnyAttribute                                                       | -                                                       | -                                                                                    | XmlAnyAttribute在一个类中只能出现最多一次                 |
| 绑定通配元素         | jakarta.xml.bind.annotation.XmlAnyElement                                                         | -                                                       | -                                                                                    |                                              |
| 映射类为简单类型       | jakarta.xml.bind.annotation.XmlValue                                                              |                                                         | com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText                       | 通常与XmlList或XmlJavaTypeAdapter合用，提示容器内元素为简单类型 |
| 映射附件           | jakarta.xml.bind.annotation.XmlAttachmentRef                                                      | -                                                       | -                                                                                    |                                              |
| 映射工厂方法到元素      | jakarta.xml.bind.annotation.XmlRegistry<br>jakarta.xml.bind.annotation.XmlElementDecl             | -                                                       | -                                                                                    |                                              |
| 解组对象生成方式       | jakarta.xml.bind.annotation.XmlType(factoryClass= , factoryMethod=)                               | -                                                       | -                                                                                    |                                              |
| 包映射XML命名空间     | jakarta.xml.bind.annotation.XmlSchema                                                             | -                                                       | -                                                                                    |                                              |
| 映射XML根元素       | jakarta.xml.bind.annotation.XmlRootElement                                                        | -                                                       | com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement                |                                              |
| 映射类到XML元素      | jakarta.xml.bind.annotation.XmlType                                                               | com.thoughtworks.xstream.annotations.XStreamAliasType   | -                                                                                    |                                              |
| 指定编组范围         | jakarta.xml.bind.annotation.XmlAccessorType                                                       | -                                                       | -                                                                                    |                                              |
| 忽略类/属性/方法      | jakarta.xml.bind.annotation.XmlTransient                                                          | com.thoughtworks.xstream.annotations.XStreamOmitField   | -                                                                                    |                                              |
| 属性编组顺序         | jakarta.xml.bind.annotation.XmlAccessorOrder<br>jakarta.xml.bind.annotation.XmlType(propOrder={}) | -                                                       | -                                                                                    |                                              |
| 属性类型映射XML内建类型  | jakarta.xml.bind.annotation.XmlSchemaType                                                         |                                                         | -                                                                                    |                                              |
| 属性编组为XML ID    | jakarta.xml.bind.annotation.XmlID                                                                 | -                                                       | -                                                                                    |                                              |
| 属性编组为XML IDREF | jakarta.xml.bind.annotation.XmlIDREF                                                              | -                                                       | -                                                                                    |                                              |
| 属性编组为base64数据  | jakarta.xml.bind.annotation.XmlMimeType<br>jakarta.xml.bind.annotation.XmlInlineBinaryData        | -                                                       | -                                                                                    |                                              |
| 枚举值编组          | jakarta.xml.bind.annotation.XmlEnum<br>jakarta.xml.bind.annotation.XmlEnumValue                   | -                                                       | -                                                                                    |                                              |
| list属性编组为简单类型  | jakarta.xml.bind.annotation.XmlList                                                               | -                                                       | -                                                                                    |                                              |
| 多态             | jakarta.xml.bind.annotation.XmlSeeAlso<br>jakarta.xml.bind.annotation.XmlElementRef               | com.thoughtworks.xstream.annotations.XStreamInclude     | -                                                                                    |                                              |
| 混杂内容           | jakarta.xml.bind.annotation.XmlMixed                                                              | -                                                       | -                                                                                    |                                              |
| 编解组带包装元素       | jakarta.xml.bind.annotation.XmlElementWrapper                                                     | com.thoughtworks.xstream.annotations.XStreamImplicit    | com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper             |                                              |
| CDATA          | -                                                                                                 | -                                                       | com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData                      |                                              |
| 自定义编解组         | jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter                                           | com.thoughtworks.xstream.annotations.XStreamConverter   | -                                                                                    |                                              |

JAXB旨在java对象和XML进行绑定，可供处理的细节较少。JAXP(Java API for XML Processing)用于在更低层次处理XML，有DOM、SAX、StaX三种API。

DOM(Document Object Model)解析可以对内容进行读写，并支持修改，但大文件解析耗费内存，甚至造成内容溢出，JDK内置了Apache Xerces作为默认的DOM解析器。

*采取DOM方式解析的类库还有jdom和dom4j，但已很久没有更新。*

SAX(Simple API for XML)作为流式解析器，开发者需要编写事件处理函数，才能得到关注的数据，JDK内置了Apache Xerces作为默认的SAX解析器。

StAX(Streaming API for XML)作为流式解析器，采用pull方式解析XML，除JDK内置的StAX实现外，woodstox也实现了StAX(同时woodstox实现了DOM和SAX)。

*采取流式拉取解析XML的库还有xpp3, xmlpull，不过已经很久没有更新。*

流式解析器不支持回溯，也不支持XPath。

参考：
1. [Jakarta EE Core Profile 10](https://jakarta.ee/specifications/coreprofile/10/)
2. [common-annotations](https://download.oracle.com/otn-pub/jcp/common_annotations-1_3-mrel3-eval-spec/jsr-250.pdf)
3. [annotations](https://jakarta.ee/specifications/annotations/2.1/annotations-spec-2.1.html)
4. [DI](https://jakarta.ee/specifications/dependency-injection/2.0)
5. [CDI](https://jakarta.ee/specifications/cdi/4.0)
6. [interceptor](https://jakarta.ee/specifications/interceptors/2.1)
7. [JSONP](https://jakarta.ee/specifications/jsonp/2.1)
8. [JSONB](https://jakarta.ee/specifications/jsonb/3.0)
9. [JAX-RS](https://jakarta.ee/specifications/restful-ws/3.1)
10. [MVC](https://jakarta.ee/specifications/mvc/2.1)
11. [JAX-RPC](https://jakarta.ee/specifications/xml-rpc/1.1)
12. [JAX_WS](https://jakarta.ee/specifications/xml-web-services/3.0)
13. [Enterprise Web Services](https://jakarta.ee/zh/specifications/enterprise-ws/2.0/)
14. [Web Service Metadata](https://jakarta.ee/specifications/web-services-metadata/3.0)
15. [SAAJ](https://jakarta.ee/specifications/soap-attachments/2.0)
16. [JAXR](https://jakarta.ee/specifications/xml-registries/1.0)
17. [JAXB](https://jakarta.ee/specifications/xml-binding/3.0/)
18. [AOP alliance](https://aopalliance.sourceforge.net/)
19. [The Main Differences between Resource Injection and Dependency Injection](https://docs.oracle.com/javaee/7/tutorial/injection003.htm)
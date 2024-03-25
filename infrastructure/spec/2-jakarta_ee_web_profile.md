# Jakarta EE Web Profile

Web概要文件（Web Profile）是企业版Java规范的一个子集，它的目标群体为web应用开发人员。

必要规范包括：Annotations, Authentication, Bean Validation, Concurrency, CDI, DI, EJB Lite, EL, Interceptors, JSONP,
JSONB, JPA, JAX-RS, Security, JSF, JSP, Servlet, JSTL, JTA, WebSocket。

由于Annotations, CDI, DI, Interceptors, JSONP, JSONB, JAX-RS已存在于核心概要文件，此处不赘述。

1. Bean Validation

Bean Validation规范用于校验bean或方法是否符合约束，它的参考实现为Hibernate Validator。

|        | Bean Validation                                                                                                | 备注                                                                   |
|:-------|:---------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------|
| 级联校验   | jakarta.validation.Valid                                                                                       |                                                                      |
| 分组     | jakarta.validation.constraints.*(groups={})                                                                    |                                                                      |
| 分组转换   | jakarta.validation.groups.ConvertGroup                                                                         |                                                                      |
| 短路校验   | jakarta.validation.GroupSequence                                                                               | 多个分组时，第一个分组校验失败后不会再校验第二个分组                                           |
| 跨参数校验  | -                                                                                                              | hibernate validator的ScriptAssert和ParameterScriptAssert支持脚本来校验多个属性/参数 |
| 覆盖约束   | jakarta.validation.OverridesAttribute                                                                          | 示例参考hibernate validator的Range约束                                      |
| 国际化消息  | *jakarta.validation.MessageInterpolator*                                                                       |                                                                      |
| 自定义消息  | jakarta.validation.constraints.*(message=)                                                                     |                                                                      |
| 自定义约束  | jakarta.validation.Constraint                                                                                  |                                                                      |
| 自定义验证  | *jakarta.validation.ConstraintValidator*<br>jakarta.validation.constraintvalidation.SupportedValidationTarget  |                                                                      |
| 校验执行时机 | jakarta.validation.executable.ValidateOnExecution                                                              |                                                                      |
| 校验对象处理 | jakarta.validation.valueextraction.UnwrapByDefault<br>jakarta.validation.valueextraction.ExtractedValue        | 配合ValueExtractor提取容器对象（尤其是List、Map、Optional等外的非泛型类型）内的对象进行校验         |
| 合并校验信息 | jakarta.validation.ReportAsSingleViolation                                                                     | 添加在自定义约束上，将多个约束违反信息合并成一个                                             |

Bean Validation支持XML配置(META-INF/validation.xml)，也支持注解，Apache BVal也是实现之一。

Apache的另一个早期项目commons-validator也可以用于校验，但只支持XML配置，缺乏分组等概念，且不兼容Bean Validation。

fluent-validator提供了注解，使得每个属性可以使用规定的接口实现来校验。为兼容规范，它使用hibernate validator来适配。

spring定义了一套自己的校验体系，需要开发者实现org.springframework.validation.Validator接口，指明负责校验的类，以及校验逻辑。
当该校验器注册到spring容器时，spring在开启校验的方法上校验参数，并将可能的违反约束信息保存到org.springframework.validation.BindingResult。

另一方面，spring也支持Bean Validation实现（如Hibernate Validator）来校验约束。
相比于spring的Validated注解支持指定校验分组，Bean Validation规范需要在使用API时指定。

2. JPA

JPA即Java Persistence API，用于规范管理持久化以及对象与关系映射。其参考实现为EclipseLink(Oracle TopLink开源贡献而来)，兼容实现有Hibernate ORM，早期版本兼容实现还有Apache OpenJPA。

|                        | JPA                                                                                                                                                                           | Hibernate ORM                                                                           | MyBatis                                                                                                                      | 备注                                                                                                      |
|:-----------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------|
| 标记为实体                  | jakarta.persistence.MappedSuperclass<br>jakarta.persistence.Entity                                                                                                            | org.hibernate.annotations.Tuplizer                                                      | ~~org.apache.ibatis.type.Alias~~                                                                                             | MappedSuperclass不能关联表                                                                                   |
| 标记为实体ID                | jakarta.persistence.Id<br>jakarta.persistence.IdClass                                                                                                                         | -                                                                                       |                                                                                                                              |                                                                                                         |
| 主键值生成方式                | jakarta.persistence.GeneratedValue                                                                                                                                            | org.hibernate.annotations.IdGeneratorType                                               | ~~org.apache.ibatis.annotations.SelectKey~~                                                                                  |                                                                                                         |
| 标记属性为生成                | -                                                                                                                                                                             | org.hibernate.annotations.Generated                                                     | -                                                                                                                            | Hibernate ORM支持使用CreationTimestamp、UpdateTimestamp注解生成值，或GeneratorType自定义                               |
| 属性值生成方式                | -                                                                                                                                                                             | org.hibernate.annotations.ValueGenerationType                                           | -                                                                                                                            |                                                                                                         |
| 忽略属性                   | jakarta.persistence.Transient                                                                                                                                                 | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 日期格式                   | jakarta.persistence.Temporal                                                                                                                                                  | -                                                                                       | -                                                                                                                            | 一般与Id、Basic或ElementCollection合用                                                                         |
| 映射枚举                   | jakarta.persistence.Enumerated                                                                                                                                                | -                                                                                       | __builtin__                                                                                                                  |                                                                                                         |
| 字符/大对象格式               | jakarta.persistence.Lob                                                                                                                                                       | org.hibernate.annotations.Nationalized                                                  | -                                                                                                                            |                                                                                                         |
| 属性为对象                  | jakarta.persistence.Embedded<br>jakarta.persistence.Embeddable                                                                                                                | -                                                                                       | -                                                                                                                            | Embeddable对象与拥有属性的实体id相同                                                                                |
| 属性为Map时标记key           | jakarta.persistence.MapKey<br>jakarta.persistence.MapKeyClass<br>jakarta.persistence.MapKeyEnumerated<br>jakarta.persistence.MapKeyTemporal                                   | org.hibernate.annotations.ManyToAny                                                     | org.apache.ibatis.annotations.MapKey                                                                                         |                                                                                                         |
| 属性为集合                  | jakarta.persistence.ElementCollection                                                                                                                                         | org.hibernate.annotations.CollectionType<br>org.hibernate.annotations.Type              | ~~/mapper/resultMap\[@collection]~~                                                                                          | JPA允许使用OrderBy指定集合排序；                                                                                   |
| 多态                     | jakarta.persistence.DiscriminatorColumn<br>jakarta.persistence.DiscriminatorValue                                                                                             | org.hibernate.annotations.DiscriminatorFormula<br>org.hibernate.annotations.Any         | org.apache.ibatis.annotations.TypeDiscriminator                                                                              | JPA默认通过列名为dtype区分类型，支持使用Inheritance来表示子类与表的关系（父子类各一张表，子类才对应表，父子类对应表关联）；Hibernate ORM支持用Target来标记接口的实现类  |
| 乐观锁                    | jakarta.persistence.Version                                                                                                                                                   | org.hibernate.annotations.OptimisticLocking<br>org.hibernate.annotations.OptimisticLock | -                                                                                                                            |                                                                                                         |
| 一对一关系                  | jakarta.persistence.OneToOne<br>jakarta.persistence.MapsId                                                                                                                    | -                                                                                       | org.apache.ibatis.annotations.One                                                                                            |                                                                                                         |
| 一对多关系                  | jakarta.persistence.OneToMany                                                                                                                                                 | -                                                                                       | org.apache.ibatis.annotations.Many                                                                                           |                                                                                                         |
| 多对一关系                  | jakarta.persistence.ManyToOne<br>jakarta.persistence.MapsId                                                                                                                   | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 多对多关系                  | jakarta.persistence.ManyToMany                                                                                                                                                | -                                                                                       | org.apache.ibatis.annotations.Many                                                                                           |                                                                                                         |
| 标记结果可缓存                | jakarta.persistence.Cacheable                                                                                                                                                 | org.hibernate.annotations.Cache                                                         | org.apache.ibatis.annotations.Options(useCache=true)                                                                         | 缓存通常还要对应失效，有JCache规范，不建议使用此注解                                                                           |
| 实体仅有有参构造函数             | jakarta.persistence.SqlResultSetMapping<br>jakarta.persistence.ConstructorResult<br>jakarta.persistence.ColumnResult                                                          | -                                                                                       | org.apache.ibatis.annotations.ConstructorArgs                                                                                |                                                                                                         |
| 结果类型映射                 | jakarta.persistence.SqlResultSetMapping<br>jakarta.persistence.EntityResult<br>jakarta.persistence.FieldResult                                                                | -                                                                                       | org.apache.ibatis.annotations.ResultType<br>org.apache.ibatis.annotations.ResultMap<br>org.apache.ibatis.annotations.Results |                                                                                                         |
| 覆盖实体关系                 | jakarta.persistence.AssociationOverride                                                                                                                                       | -                                                                                       | -                                                                                                                            | 覆盖MappedSuperClass或Embedded定义的关系                                                                        |
| 覆盖属性                   | jakarta.persistence.AttributeOverride                                                                                                                                         | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 自定义对象与关系的转换            | jakarta.persistence.Converter<br>jakarta.persistence.Convert                                                                                                                  | org.hibernate.annotations.Type<br>org.hibernate.annotations.Columns                     | org.apache.ibatis.type.MappedTypes<br>org.apache.ibatis.type.MappedJdbcTypes                                                 |                                                                                                         |
| SQL-insert             | jakarta.persistence.NamedNativeQuery<br>jakarta.persistence.QueryHint                                                                                                         | org.hibernate.annotations.SQLInsert                                                     | org.apache.ibatis.annotations.Insert<br>org.apache.ibatis.annotations.InsertProvider                                         | 支持数据库厂商的hint；HibernateORM支持DynamicInsert；MyBatis支持动态SQL                                                 |
| SQL-delete             | jakarta.persistence.NamedNativeQuery<br>jakarta.persistence.QueryHint                                                                                                         | org.hibernate.annotations.SQLDelete<br>org.hibernate.annotations.SQLDeleteAll           | org.apache.ibatis.annotations.Delete<br>org.apache.ibatis.annotations.DeleteProvider                                         | 支持数据库厂商的hint；MyBatis支持动态SQL                                                                             |
| SQL-update             | jakarta.persistence.NamedNativeQuery<br>jakarta.persistence.QueryHint                                                                                                         | org.hibernate.annotations.SQLUpdate<br>org.hibernate.annotations.Where                  | org.apache.ibatis.annotations.Update<br>org.apache.ibatis.annotations.UpdateProvider                                         | 支持数据库厂商的hint；HibernateORM支持DynamicUpdate；MyBatis支持动态SQL                                                 |
| SQL-select             | jakarta.persistence.NamedNativeQuery<br>jakarta.persistence.QueryHint                                                                                                         | org.hibernate.annotations.NamedNativeQuery<br>org.hibernate.annotations.Where           | org.apache.ibatis.annotations.Select<br>org.apache.ibatis.annotations.SelectProvider                                         | 支持数据库厂商的hint；MyBatis支持动态SQL                                                                             |
| JPQL/HQL               | jakarta.persistence.NamedQuery<br>jakarta.persistence.QueryHint                                                                                                               | org.hibernate.annotations.NamedQuery<br>org.hibernate.annotations.Loader                | -                                                                                                                            |                                                                                                         |
| 存储过程                   | jakarta.persistence.NamedStoredProcedureQuery<br>jakarta.persistence.StoredProcedureParameter                                                                                 | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 参数绑定                   | -                                                                                                                                                                             | -                                                                                       | org.apache.ibatis.annotations.Param                                                                                          | JPA支持顺序或命名参数                                                                                            |
| 关联查询                   | jakarta.persistence.NamedEntityGraph<br>jakarta.persistence.NamedSubgraph<br>jakarta.persistence.NamedAttributeNode                                                           | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 注入EntityManagerFactory | jakarta.persistence.PersistenceUnit                                                                                                                                           | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 注入EntityManager        | jakarta.persistence.PersistenceContext                                                                                                                                        | -                                                                                       | -                                                                                                                            | JPA的EntityManager非线程安全，可以结合PersistenceProperty提供额外信息                                                    |
| 标记实体监听器                | jakarta.persistence.EntityListeners                                                                                                                                           | -                                                                                       | -                                                                                                                            | 配合ExcludeDefaultListeners或ExcludeSuperclassListeners排除不想要的                                              |
| 持久化前回调                 | jakarta.persistence.PrePersist                                                                                                                                                | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 持久化后回调                 | jakarta.persistence.PostPersist                                                                                                                                               | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 移除前回调                  | jakarta.persistence.PreRemove                                                                                                                                                 | -                                                                                       | -                                                                                                                            | MyBatis支持开发者通过Intercepts编写代码拦截Executor/ParameterHandler/ResultHandler/StatementHandler                  |
| 移除后回调                  | jakarta.persistence.PostRemove                                                                                                                                                | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 更新前回调                  | jakarta.persistence.PreUpdate                                                                                                                                                 | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 更新后回调                  | jakarta.persistence.PostUpdate                                                                                                                                                | -                                                                                       | -                                                                                                                            |                                                                                                         |
| 加载后回调                  | jakarta.persistence.PostLoad                                                                                                                                                  | -                                                                                       | -                                                                                                                            |                                                                                                         |
| schema生成: 表            | jakarta.persistence.Table<br>jakarta.persistence.SecondaryTable<br>jakarta.persistence.CollectionTable<br>jakarta.persistence.JoinTable<br>jakarta.persistence.TableGenerator | org.hibernate.annotations.Table                                                         | -                                                                                                                            | 一个实体对应1个以上表时用SecondaryTable标识非主表                                                                        |
| schema生成: 主键           | jakarta.persistence.Id<br>jakarta.persistence.EmbeddedId                                                                                                                      | org.hibernate.annotations.NaturalId                                                     | ~~org.apache.ibatis.annotations.Result(id=true)~~                                                                            | 可配合GeneratedValue声明主键非开发者提供，而是JPA实现生成                                                                   |
| schema生成: 唯一键          | jakarta.persistence.UniqueConstraint                                                                                                                                          | -                                                                                       | -                                                                                                                            |                                                                                                         |
| schema生成: 外键           | jakarta.persistence.ForeignKey<br>jakarta.persistence.PrimaryKeyJoinColumn<br>jakarta.persistence.MapKeyJoinColumn<br>jakarta.persistence.JoinColumn                          | -                                                                                       | -                                                                                                                            |                                                                                                         |
| schema生成: 列            | jakarta.persistence.Column<br>jakarta.persistence.MapKeyColumn<br>jakarta.persistence.OrderColumn<br>jakarta.persistence.DiscriminatorColumn                                  | -                                                                                       | ~~org.apache.ibatis.annotations.Result(column=, property=)~~                                                                 | Hibernate ORM支持用ColumnTransformer进行读后/写前处理，使用Formula进行计算                                                |
| schema生成: 索引           | jakarta.persistence.Index                                                                                                                                                     | -                                                                                       | -                                                                                                                            |                                                                                                         |
| schema生成: 约束           | -                                                                                                                                                                             | org.hibernate.annotations.Check                                                         | -                                                                                                                            |                                                                                                         |
| schema生成: 默认值          | -                                                                                                                                                                             | org.hibernate.annotations.ColumnDefault                                                 | -                                                                                                                            |                                                                                                         |
| schema生成: sequence     | jakarta.persistence.SequenceGenerator                                                                                                                                         | -                                                                                       | -                                                                                                                            |                                                                                                         |

JPA支持使用文件进行配置(META-INF/persistence.xml)。

JPA的API和Hibernate ORM非常相似，Hibernate ORM做另外一些补充，如实体加载用的过滤器org.hibernate.annotations.FilterDefs和org.hibernate.annotations.Filters，如软删除。

Hibernate ORM对审计做了部分支持（org.hibernate.annotations.CreationTimestamp、org.hibernate.annotations.UpdateTimestamp），甚至对SaaS所需的租户（org.hibernate.annotations.TenantId）也有支持。

Spring Data JPA依赖Hibernate ORM，支持锁：org.springframework.data.jpa.repository.Lock，对审计有增强：org.springframework.data.annotation.CreatedBy、org.springframework.data.annotation.CreatedDate、org.springframework.data.annotation.LastModifiedBy、org.springframework.data.annotation.LastModifiedDate。
此外，Spring Data JPA支持领域驱动设计：org.springframework.data.domain.DomainEvents、org.springframework.transaction.event.TransactionalEventListener、org.springframework.data.domain.AfterDomainEventPublication。
Spring Data JPA可以根据符合约定的方法名自动生成对应的SQL，简化了开发者的使用。

制定中的Jakarta Data规范意图基于Jakarta Persistence规范和Jakarta NoSQL规范，作为囊括SQL、NoSQL和Web service的数据访问层，似乎参照了Spring Data JPA，都支持将接口作为Repository，允许CDI注入，支持根据方法名生成SQL或指定SQL(Jakarta Data允许因不同的数据源和供应商而使用SQL、JPQL、CQL等)。

__JPA在面向对象角度，用Java语言的注解来生成数据库相关Schema，甚至尝试隐藏SQL。__

__从实际工程来看，简单的查询，为了性能考虑，会区分出不带大字段和带大字段的。
为了支持查询条件和排序、分页，存在动态SQL或Query By Example的必要，否则会存在较多的方法在DAO类中(spring-data-jpa支持从方法名推导)。
一对多、多对多的关系映射，这些JPA基本都能一定程度满足。
但对于复杂的报表查询，还是得使用SQL。即便用其他形式代替，目前来看，是一种新的复杂方式(JPQL)隐藏原有的复杂性，得不偿失。__

3. JTA

JTA即Java Transaction API，它在一个事务管理器视角定义高级别API，包括为事务应用标记事务边界的API和为应用服务器管理事务边界的事务管理API。
常见的JTA开源实现有[Atomikos](https://www.atomikos.com/Main/WebHome) 、~~Bitronix~~、[JBoss Narayana](https://www.narayana.io) 等。

一般来说，分布式事务由5个主要成分：事务管理器(Transaction Manager)提供标记事务边界、事务资源管理、同步、事务上下文传播等服务和管理功能；
应用服务器(Application Server)提供包含事务状态管理等应用运行所需环境的基础结构；
资源管理器(Resource Manager)为应用提供资源访问能力，资源管理器通过实现事务管理器的相关事务资源接口来参与分布式事务；
一个事务应用程序，它基于应用服务器，并声明事务属性；
通信资源管理器(Communication Resource Manager)支持事务上下文传播、对传入和传出事务的访问。

|        | JTA                                                       | spring-tx                                                                     |
|:-------|:----------------------------------------------------------|:------------------------------------------------------------------------------|
| 事务范围标记 | jakarta.transaction.TransactionScoped                     | -                                                                             |
| 开启事务   | jakarta.transaction.Transactional                         | org.springframework.transaction.annotation.Transactional                      |
| 回滚条件   | jakarta.transaction.Transactional(rollbackOn=)            | org.springframework.transaction.annotation.Transactional(rollbackFor={})      |
| 事务传播   | *jakarta.transaction.Transactional.TxType*                | org.springframework.transaction.annotation.Propagation                        |
| 事务同步钩子 | *jakarta.transaction.Synchronization*                     | *org.springframework.transaction.support.TransactionSynchronization*          |
| 事务钩子注册 | *jakarta.transaction.TransactionSynchronizationRegistry*  | *org.springframework.transaction.support.TransactionSynchronizationManager*   |
| 事务管理器  | *jakarta.transaction.TransactionManager*                  |                                                                               |
| 程序事务管理 | *jakarta.transaction.UserTransaction*                     |                                                                               |
| 管理监听   | -                                                         | org.springframework.transaction.event.TransactionalEventListener              |
| 数据库异常  | -                                                         | *org.springframework.dao.DataAccessException*                                 |
| 事务异常   | *jakarta.transaction.TransactionalException*              | *org.springframework.transaction.TransactionException*                        |

4. Security

Authentication(旧版本也称之为JASPI或JASPIC，即Java Authentication SPI for Containers)和Authorization(旧版本也称之为JACC，即Java Authorization Contract for Containers)作为底层API，供容器服务商实现。Security的参考实现为Eclipse Soteria。

Authentication规范规定从AuthConfigFactory得到AuthConfigProvider。
如果是客户端，则从中获取ClientAuthConfig，根据认证主体，获取ClientAuthContext，然后对请求进行处理，确保请求发送前达到安全要求。当得到响应时，进行验证。
如果是服务端，则从中获取ServerAuthConfig，根据认证主体，获取ServerAuthContext，然后对接收到的请求进行验证，再交给业务代码处理，最后在发送响应消息前进行处理。

Authorization规范要求必须在Servlet容器或EJB容器中，规范可分为三个子约定：供应商配置子约定(Provider Configuration Subcontract)、 策略配置子约定(Policy Configuration Subcontract)和策略决策和执行子约定(Policy Decision and Enforcement Subcontract)。
在供应商子约定中要求安全策略必须是java.security.Policy的子类，许可为定义在"jakarta.security.jacc"包下java.security.Permission的子类，策略配置通过PolicyConfigurationFactory得到PolicyConfiguration。
容器可以将PolicyContextHandler注册到PolicyContext，容器使用PolicyContextHandler可以做自定义的安全策略决策。
在策略配置子约定中定义了容器部署工具和容器供应商间转换声明式Jakarta EE鉴权策略为Java SE安全策略声明。
在策略决策和执行子约定中定义容器安全策略决执行点和Jakarta EE规范要求的容器供应商需实现的策略决策间的交互，如servlet容器中根据URL匹配对应的WebResourcePermission，然后执行安全策略。

Security规范定义了调用者(Caller，可以是请求应用的用户或者API)携带委托(Caller Principle)访问应用资源，应用获取凭据(Credential)，并与身份存储(Identity Store)中信息进行比对。
如果比对通过，则从身份存储信息获取属性构成已认证主体(Subject)供后续使用，否则认证机制报告认证失败，提示用户未登入无法鉴别用户是否有权限访问该资源。

|          | Authentication & Authorization & Security                                                                                                          | spring-security                                                                               | Apache Shiro                                                      | 备注                                                                        |
|:---------|:---------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------|:------------------------------------------------------------------|:--------------------------------------------------------------------------|
| 认证配置     | *jakarta.security.auth.message.config.AuthConfig*                                                                                                  | -                                                                                             | -                                                                 |                                                                           |
| 客户端认证上下文 | *jakarta.security.auth.message.config.ClientAuthContext*                                                                                           | -                                                                                             | -                                                                 |                                                                           |
| 服务端认证上下文 | *jakarta.security.auth.message.config.ServerAuthContext*                                                                                           | -                                                                                             | -                                                                 |                                                                           |
| 鉴权配置     | *jakarta.security.jacc.PolicyConfigurationFactory*<br>*jakarta.security.jacc.PolicyConfiguration*                                                  | -                                                                                             | -                                                                 |                                                                           |
| 策略上下文处理  | *jakarta.security.jacc.PolicyContextHandler*                                                                                                       | -                                                                                             | -                                                                 |                                                                           |
| 资源许可     | *jakarta.security.jacc.WebResourcePermission*<br>*jakarta.security.jacc.EJBMethodPermission*                                                       | org.springframework.security.access.prepost.PreAuthorize("hasAuthority('permission:action')") | org.apache.shiro.authz.annotation.RequiresPermissions             | spring-security支持自定义bean鉴权：@PreAuthorize("@beanName.method('parameter')") |
| 角色许可     | *jakarta.security.jacc.WebRoleRefPermission*<br>*jakarta.security.jacc.EJBRoleRefPermission*                                                       | org.springframework.security.access.prepost.PreAuthorize("hasRole('ROLE_')")                  | org.apache.shiro.authz.annotation.RequiresRoles                   | Shiro支持用户许可方式                                                             |
| 数据许可     | *jakarta.security.jacc.WebUserDataPermission*                                                                                                      | -                                                                                             | -                                                                 |                                                                           |
| 调用者委托    | *jakarta.security.enterprise.CallerPrincipal*                                                                                                      | org.springframework.security.core.annotation.AuthenticationPrincipal                          | *org.apache.shiro.subject.PrincipalCollection*                    |                                                                           |
| 表单认证     | jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition                                                    | *org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter*         | *org.apache.shiro.web.filter.authc.FormAuthenticationFilter*      | JakartaEE支持EL，spring-security支持SpEL                                       |
| 自定义表单认证  | jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition                                              | -                                                                                             | ~~org.apache.shiro.web.filter.authc.AuthenticatingFilter~~        |                                                                           |
| BASIC认证  | jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition                                                   | *org.springframework.security.web.authentication.www.BasicAuthenticationFilter*               | *org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter* | spring-security/shiro内置digest、Bearer token等认证方式                           |
| OpenId认证 | jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition                                                  | __OAuth__                                                                                     | -                                                                 |                                                                           |
| 记住       | jakarta.security.enterprise.authentication.mechanism.http.RememberMe                                                                               | *org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter*   | *org.apache.shiro.mgt.RememberMeManager*                          |                                                                           |
| session  | jakarta.security.enterprise.authentication.mechanism.http.AutoApplySession                                                                         | *org.springframework.security.web.session.SessionManagementFilter*                            | *org.apache.shiro.session.mgt.SessionManager*                     |                                                                           |
| 登出       | -                                                                                                                                                  | *org.springframework.security.web.authentication.logout.LogoutFilter*                         | *org.apache.shiro.web.filter.authc.LogoutFilter*                  |                                                                           |
| 凭据       | *jakarta.security.enterprise.credential.Credential*                                                                                                | __java.lang.Object__                                                                          | *org.apache.shiro.authc.AuthenticationToken*                      |                                                                           |
| 身份存储     | jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition<br>jakarta.security.enterprise.identitystore.LdapIdentityStoreDefinition | *org.springframework.security.authorization.AuthorizationManager*                             | *org.apache.shiro.realm.Realm*                                    |                                                                           |

Spring Security项目和Apache Shiro项目主要用于servlet环境，以Filter形式拦截请求，进行认证和鉴权。他们一般会提供丰富的认证、鉴权方式，此外还有支持一些高级的加密算法。

Servlet规范定义了ServletSecurity注解，它允许在类上声明类和方法允许访问的角色（及缺失角色时默认的行为是允许还是阻止）。

5. Servlet & JSP & JSF

JSP即JavaServer Pages，它为web应用定义了一个支持HTML、自定义标签、表达式语言和内嵌java代码的模板引擎，这些混合内容可被编译为servlet。参考实现为Eclipse WaSP。

JSP被编译后生成Servlet(包名前缀由供应商决定，结合目录，文件名一般由"_jsp"和原文件名组合而成)，web容器加载Servlet类时创建实例并初始化(jspInit)，然后调用执行方法(_jspService)，在销毁时回调销毁方法(jspDestroy)。
JSP规范建议文件后缀使用".jsp"作为可直接渲染的模板，".jspf"作为可被包含的片段，".jspx"作为渲染为XML文档。
JSP规范允许开发者自定义tag，tag文件后缀命名建议同JSP文件。tag文件的DTD文件应当在"META-INF/tags"，war包则应在"WEB-INF/tags"。
部署人员可以在web.xml中通过taglib标签来指定位置。

JSP动作在请求处理阶段执行，标准动作有useBean, setProperty, getProperty, include, forward, param, plugin, params, fallback, attribute, body, invoke, doBody, element, text, output, root, declaration, scriptlet, expression。

JSF即JavaServer Faces，它定义了MVC框架中构建web应用UI所需的UI组件、状态管理、事件处理、输入校验、页面导航、国际化和可访问性等。它的参考实现为Eclipse Mojarra。
JSF集成ajax技术，支持对UI事件进行响应。和spring-web-flow类似，可以定义页面流程。

*JSF内置的校验API已关联Bean Validation*

|       | JSP & JSTL & EL                                                                      | JSF & JSTL & EL                                                                             | thymeleaf                                           | 备注                                                                               |
|:------|:-------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------|:----------------------------------------------------|:---------------------------------------------------------------------------------|
| 特殊指令  | <%@  %>                                                                              | &#60;html xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/core"> | &#60;html xmlns:th="http://www.thymeleaf.org">      | JSP指令有page, taglib, include等                                                     |
| 脚本代码  | <%  %><br>&#60;jsp:scriptlet&#62;&#60;/jsp:scriptlet&#62;                            |                                                                                             |                                                     |                                                                                  |
| 声明    | <%!  %><br>__JSTL: <c:set var="" value="" />__                                       | __JSTL: <c:set var="" value="" />__                                                         | th:with=""<br>data-th-with=""                       |                                                                                  |
| 预定义变量 | request, response, pageContext, session, application, out, config, page<br>exception |                                                                                             | ctx, vars, root, local, param, session, application | JSF可以使用Managed Bean；Thymeleaf还定义了objects等常用类型作为预定义工具                             |
| 取值    | <%= %><br>__JSTL: <c:out value="" />__<br>__EL: ${var}或#{var}__                      | __EL: ${var}或#{var}__                                                                       | ${}                                                 | Thymeleaf支持省略上下文取值:*{}                                                           |
| 条件判断  | <% if(bool){scriptlet} %><br>__JSTL: <c:if test="" />__                              |                                                                                             | th:if=""<br>data-th-if=""                           |                                                                                  |
| 循环    | <% while(orFor){scriptlet} %><br>__JSTL: <c:forEach items="" var="">__               |                                                                                             | th:each=""<br>data-th-each=""                       |                                                                                  |
| 重用片段  | <%@ include file="" %><br><jsp:include page="">                                      |                                                                                             | ~{}                                                 | include在编译时包含"jsp:include"在请求时包含； Thymeleaf支持包含整个模板，或部分：@{templateFile:selector} |
| URL   | __JSTL: <c:url value="" />__                                                         |                                                                                             | @{}                                                 |                                                                                  |
| 国际化   | __JSTL: <fmt:message key="" bundle="" />__                                           | <f:loadBundle basename="" var="" />                                                         | #{}                                                 |                                                                                  |
| 生成内容  | html, xml                                                                            | xhtml                                                                                       | html, xml                                           | Thymeleaf支持两种标记语言，三种文本模板(txt, js, css)                                           |

Servlet规范目前支持使用注解来标记(仍需实现相关接口)Servlet、Filter、ServletContextListener而不用在web.xml声明，jar包可以在"META-INF/web-fragment.xml"声明自己的组件作为web.xml的补充。
同时，可以设置"metadata-complete=true"来忽略这些注解和web-fragment.xml。

JSTL即Java Standard Tag Library，它定义了五个标准标签：core, functions, fmt, sql, xml。
JSF定义了两个标签：jakarta.faces.core、jakarta.faces.html。不同于基于Action（由应用代码将HTTP请求路由到控制器）的MVC规范，JSF为基于构件的框架，由框架完成控制器职能。
相比于JSTL以单独的标签形式存在于jsp文件，Thymeleaf标记通以属性(&#60;htmlTag th:dialect=""&#62;)方式存在于html，方便预览。对于需要动态生成的内容，可以通过"th:attr"来覆盖属性；对于条件生成，可以使用"th:unless"或"th:switch"；对于循环，可以使用"th:each"。

模板引擎还有许多，如Apache Velocity、Apache Freemarker，作为通用模板引擎，它们可以脱离servlet环境，不仅可以生成html，还可以生成程序代码或其它模板性质的东西。

6. EL

表达式语言(Expression Language)一开始被设计为web表现层的简单语言，用于取值、表达式求值、操作符求值、静态方法调用等场景。EL参考实现为Eclipse Expressly(之前为sun-el，或称之为uel-ri)。

|                 | EL                                           | SpEL                                         | OGNL                                                         | 备注                                                          |
|:----------------|:---------------------------------------------|:---------------------------------------------|:-------------------------------------------------------------|:------------------------------------------------------------|
| eval-expression | ${}, #{}                                     | #{}                                          | #                                                            | EL中不能同时使用，不支持嵌套                                             |
| 属性              | [], .                                        | [], .                                        | .                                                            | EL遵循ECMAScript约定，Spring EL支持Groovy方式避免空指针: '?.'             |
| 数学运算符           | +, -, *, /, %, div, mod                      | +, -, *, /, %, ^, div, mod                   | +, -, *, /, %, ^, <<, >>, >>>                                |                                                             |
| 位运算符            |                                              |                                              | &, &#124;, ~, ^, <<, >>, >>>, band, bor, xor, shl, shr, ushr |                                                             |
| 字符串连接           | +=                                           |                                              |                                                              |                                                             |
| 关系运算符           | ==, !=, <, >, <=, >=, eq, ne, lt, gt, le, ge | ==, !=, <, >, <=, >=, eq, ne, lt, gt, le, ge | ==, !=, <, >, <=, >=, eq, neq, lt, gt, lte, gte              |                                                             |
| 逻辑运算符           | &&, &#124;&#124;, !, and, or, not            | &&, &#124;&#124;, !, and, or, not            | &&, &#124;&#124;, !, and, or, not                            |                                                             |
| 空判断             | empty                                        |                                              |                                                              |                                                             |
| 赋值              | =                                            | =                                            | =                                                            | 赋值仅用于lvalue                                                 |
| 条件判断            | ? :                                          | ? :                                          | ? :                                                          | Spring EL和支持Elvis变形：即'a ? a : b'等价于'a ?: b'                 |
| 分号运算符           | ;                                            |                                              | ,                                                            | 先计算前一个，返回后一个                                                |
| 括号              | ()                                           | ()                                           | ()                                                           | 改变优先级，括号内的先计算                                               |
| 函数              | ns:fn(args), fn(args)                        | #fn(args)                                    | varName.fn(args)                                             | 需要FunctionMapper实现存在，Spring EL需先将静态方法绑定到变量                  |
| 变量              | varName = {"map":{"array":\[{"set"}]}}       | varName = {"map":{"array":{{"set"}}}}        | #varName = #{"map":{"array":{"no prefix #"}}}                | 需要VariableMapper实现存在，Spring EL内置'#this'和'#root'             |
| lambda表达式       | (args) -> ELExpression                       |                                              | ~~pseudo-lambda~~                                            |                                                             |
| 静态字段或方法         | class.s_t_a_t_i_c                            | T(c.l.a.s.s).s_t_a_t_i_c                     | @c.l.a.s.s@s_t_a_t_i_c                                       | EL限制：非"java.lang"包下的类需开发者显示导入，字段/方法必须是public、static，字段不能被修改 |
| 实例化             | class(args)                                  | new c.l.a.s.s(args)                          | new c.l.a.s.s(args)                                          | EL限制同上                                                      |
| 引用bean          |                                              | @beanName                                    |                                                              | Spring EL要先在表达式求值上下文设置BeanResolver                          |
| 转义              | \                                            |                                              |                                                              |                                                             |

对于JSP，只是用到了rvalue，即将表达式求值给左边的标签处理。而对于JSF，还有lvalue，即将数据当作参数交给表达式处理。
EL通过在流上使用lambda操作来实现筛选(selection)和投影(projection)，保持和java语法的一致性。

Spring EL在功能上有增强，比如可以使用上下文的bean，支持matches关键字来正则匹配，吸收Groovy的语法糖来避免空指针，以及支持筛选(collection.?\[prop op value])和投影(collection.!\[prop])。

OGNL遵循了java中instanceof关键字用途，提供了筛选(collection.{? prop op value})和投影(collection.{prop})。

__MVEL和JEXL实现了脚本引擎规范(JSR 223)，定位更偏向脚本语言，avaitor也逐渐往脚本语言方向发展，此处不做比较。commons-el项目已很久没有更新，此处不做比较。__

7. websocket

Jakarta WebSocket规范为WebSocket协议定义客户端和服务端端点API，参考实现为Eclipse Tyrus。

WebSocket通过在HTTP协议上使用特定请求头(Upgrade: websocket)沟通一致后切换，连接的双方是对等的节点(peer)，可以互相发送消息。
代表任意一端的一系列交互称为端点(endpoint)，发起连接的为客户端端点(client endpoint)，接收连接的为服务端端点(server endpoint)。
而连接两端使用WebSocket协议的网络连接称为WebSocket连接(connection)。连接建立后，使用会话(session)来维持一个节点和一系列端点的交互。

|         | websocket                               | Jetty                                                          |
|:--------|:----------------------------------------|:---------------------------------------------------------------|
| 服务端端点   | jakarta.websocket.server.ServerEndpoint | org.eclipse.jetty.websocket.api.annotations.WebSocket          |
| 路径参数    | jakarta.websocket.server.PathParam      |                                                                |
| 事件-打开会话 | jakarta.websocket.OnOpen                | org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect |
| 事件-关闭会话 | jakarta.websocket.OnClose               | org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose   |
| 消息      | jakarta.websocket.OnMessage             | org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage |
| 帧       | -                                       | org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame   |
| 异常处理    | jakarta.websocket.OnError               | org.eclipse.jetty.websocket.api.annotations.OnWebSocketError   |
| 客户端端点   | jakarta.websocket.ClientEndpoint        |                                                                |

spring-websocket承担适配器角色，将自定义的xml或API进行解析，适配标准WebSocket API(jakarta.websocket.Endpoint, jakarta.websocket.Session)，交给jetty、tomcat等支持WebSocket的web容器处理。
对于不支持WebSocket的场景，spring-websocket提供了备选方案SockJS：通过请求"/info"获取服务器信息，决定是使用WebSocket，还是使用HTTP Streaming，或者HTTP Long Pulling。

WebSocket定义了文本消息和二进制消息，同时提供了自定义Encoder和Decoder来绑定对象，但在分布式场景下，server endpoint所在应用并不一定是最终的上游。
需要一定机制，在异步情形下，更上游服务能够尽量少的次数，尽可能的将响应给到持有会话的节点。spring-websocket支持通过STOM(Simple Text Oriented Messaging Protocol)来与消息代理打通。

8. Concurrency

并发规范提供了在保留Jakarta EE平台优势的同时，不影响容器完整性的情况下，提升用用程序并发性。

|       | Concurrency                                                  | spring-core                                            |
|:------|:-------------------------------------------------------------|:-------------------------------------------------------|
| 线程池   | *jakarta.enterprise.concurrent.ManagedExecutorService*       | *org.springframework.core.task.TaskExecutor*           |
| 线程工厂  | jakarta.enterprise.concurrent.ManagedThreadFactoryDefinition | -                                                      |
| 线程    | *jakarta.enterprise.concurrent.ManagedTask*                  | ~~java.lang.Runnable~~                                 |
| 线程上下文 | *jakarta.enterprise.concurrent.spi.ThreadContextProvider*    | -                                                      |
| 任务触发  | *jakarta.enterprise.concurrent.Trigger*                      | org.springframework.scheduling.annotation.Scheduled    |
| 异步任务  | jakarta.enterprise.concurrent.Asynchronous                   | org.springframework.scheduling.annotation.Async        |
| 上下文任务 | *jakarta.enterprise.concurrent.ContextService*               | *org.springframework.core.task.TaskDecorator*          |
| 任务监听  | *jakarta.enterprise.concurrent.ManagedTaskListener*          | *org.springframework.util.concurrent.ListenableFuture* |

规范建议ManagedExecutorService引用放在"java:comp/env/concurrent"命名空间下。

参考：

1. [Jakarta EE Web Profile 10](https://jakarta.ee/specifications/webprofile/10/)
2. [Servlet](https://jakarta.ee/specifications/servlet/6.0)
3. [JSP](https://jakarta.ee/specifications/pages/3.1)
4. [JSTL](https://jakarta.ee/specifications/tags/3.0)
5. [EL](https://jakarta.ee/specifications/expression-language/5.0)
6. [JSF](https://jakarta.ee/specifications/faces/4.0)
7. [JTA](https://jakarta.ee/specifications/transactions/2.0)
8. [JPA](https://jakarta.ee/specifications/persistence/3.1)
9. [Bean Validation](https://jakarta.ee/specifications/bean-validation/3.0)
10. [WebSocket](https://jakarta.ee/specifications/websocket/2.1)
11. [Security](https://jakarta.ee/specifications/security/3.0)
12. [Authentication](https://jakarta.ee/specifications/authentication/3.0)
13. [Authorization](https://jakarta.ee/specifications/authorization/2.0)
14. [Concurrency](https://jakarta.ee/specifications/concurrency/3.0)
15. [Spring Expression Language](https://docs.spring.io/spring-framework/reference/core/expressions.html)
16. [MVEL](http://mvel.documentnode.com/)
17. [Hibernate ORM](https://access.redhat.com/documentation/en-us/jboss_enterprise_application_platform/5/html-single/hibernate_annotations_reference_guide/index#entity)
18. [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html)
19. [Spring WebSocket](https://docs.spring.io/spring/reference/web/websocket.html)
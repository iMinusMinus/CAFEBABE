# 经典开源项目重制

1. 目标

   最小依赖下，将经典Java开源项目重构，满足类库需求。

2. 方法

   + 替换。使用JDK的API将开源项目的依赖缩小，如使用Objects.requireNonNull替换guava的Preconditions.checkNotNull；
     如使用if (expression) { throw new IllegalArgumentException() }替换Preconditions.checkArgument。
   + 内联。复制开源项目代码，替换方法调用来消除依赖。
   + 复制。当该类为核心类，或者被频繁引用且代码行较多时，可考虑直接复制。

3. 范围

   + guava 
     + BloomFilter. 注意与Redis的BloomFilter算法兼容！
     + RateLimiter
   + ~~bouncy castle~~, JCE缺少国密算法（如SM2非对称加密、SM3消息摘要算法、SM4对称加密算法），但JCE会验证jar包签名
   + jackson
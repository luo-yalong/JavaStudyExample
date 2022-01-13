# 秒杀系统

## 秒杀系统

### 秒杀场景

- 电商抢购商品
- 火车票抢座 12306
- ……

### 为什么做个系统

**核心问题**

- 严防商品超卖

### 保护措施有哪些

- 乐观锁防止超卖 --核心--
- 令牌桶限流
- Redis 缓存
- 消息队列一步处理订单

```java
public void main(){
  String abc = "hello";
}
```



## 防止超卖

### 数据库表

```mysql
drop table if exists stock;
create table stock (
	id int(11) unsigned not null auto_increment,
  name varchar(50) not null default '' comment '名称',
  count int(11) not null comment '库存',
  sale int not null comment '已售',
  version int not null comment '乐观锁、版本号',
  primary key(id)
) engine=innodb default charset=utf8;

drop table if exists stock_order;
create table stock_order(
	id int unsigned not null auto_increment,
  sid int not null comment '库存id',
  name varchar(30) not null default '' comment '商品名称',
  create_time timestamp not null default current_timestamp on update current_timestamp comment '创建时间',
  primary key(id)
)engine=innodb default charset=utf8;
```

## 漏斗算法和令牌桶算法

### 令牌桶的使用

> 使用令牌桶

#### 引入依赖

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
    <scope>compile</scope>
</dependency>
```

#### 使用

限流一般是放在控制器内

```
//创建令牌桶实例
RateLimiter limiter = RateLimiter.create(10);
//代表每秒产生10个令牌

//使用

//没有获取令牌的请求，会一直等待获取令牌
limiter.acquire();

//2. 设置等待时间，如果在等待时间内获取到了令牌，则处理业务。否则，抛弃
limiter.tryAcquire(2,TimeUnit.SECOND);
```

#### 基本使用

1. 阻塞直到获取令牌

```java
//创建实例：每秒种产生20个令牌
private RateLimiter limiter = RateLimiter.create(20);

@GetMapping("/sale")
public String sale(){
    //获取到令牌的请求，执行业务方法，没有获取到令牌的请求，直到获取到令牌，执行业务方法
    log.info("令牌等待时间: " + limiter.acquire());
    //执行业务
    try {
        TimeUnit.MILLISECONDS.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "成功";
}
```

2. 超时抛弃

```java
//创建实例：每秒种产生20个令牌
private RateLimiter limiter = RateLimiter.create(20);

@GetMapping("/sale")
public String sale(){
    //获取到令牌的请求，执行业务方法，没有获取到令牌的请求，直到获取到令牌，执行业务方法
    //log.info("令牌等待时间: " + limiter.acquire());

    //请求在2秒钟内没有获取到令牌，会直接抛弃请求
    if (!limiter.tryAcquire(2, TimeUnit.SECONDS)){
        log.info("抢购失败：服务器进行了限流");
    }

    log.info("执行业务");
    try {
        TimeUnit.MILLISECONDS.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "成功";
}
```

### 改造控制器方法

```java
@GetMapping("/killToken")
public String killToken(Integer id){

    if (!limiter.tryAcquire(3, TimeUnit.SECONDS)){
        log.info("抢购失败：活动太火爆了，请重试！！！");
    }

    System.out.println("秒杀商品的id = " + id);
    //处理秒杀业务逻辑
    try {
        int orderId = orderService.kill(id);
        log.info("秒杀成功：订单ID = " + orderId);
        return "秒杀成功：订单ID = " + orderId;
    } catch (Exception e) {
        //e.printStackTrace();
        log.info("抢购失败：" + e.getMessage());
        return e.getMessage();
    }
}
```

## 隐藏秒杀接口

​		目前已经完成了防止商品超卖和接口限流，已经能够方法大流量把我们的服务器直接搞炸。我们需要关心一些细节问题。

1. 我们应该在一定时间内执行秒杀处理，不能在任意时间都接受秒杀请求，如果加入时间验证？
2. 对于稍微懂点电脑的人来说，又会动用歪脑筋通过抓包的方式来获取我们的接口地址。然后通过脚本来进行抢购怎么办？
3. 秒杀开始之后如何限制单个用户的请求频率，即单位时间内限制访问次数？

**主要内容**

- 限时抢购
- 抢购接口隐藏
- 单用户限制频率（单位时间内限制访问次数）

### 限时抢购的实现

​		使用`redis`来记录秒杀商品的时间，对秒杀过期的请求进行过期处理


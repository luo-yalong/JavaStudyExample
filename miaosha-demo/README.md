# 商品秒杀

## 1. 秒杀系统

### 1.1 使用场景

- 电商抢购商品
- 火车票抢座 12306
- ……

### 1.2 核心问题

- 严防商品超卖

### 1.3 保护措施

- 乐观锁防止超卖
- 令牌桶限流
- Redis 缓存
- 消息队列异步处理订单

> 其主要目的：防止超卖

## 2. 搭建环境

### 2.1 创建数据库

需要两个表：`库存表` 和 `订单表`

打开Navicat创建一个 名字为 `ms` 的数据库。之后，点击新建查询，复制一下 `sql` 语句创建数据表。

```mysql
drop table if exists stock;
CREATE TABLE `stock` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `count` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `sale` int(11) NOT NULL DEFAULT '0' COMMENT '已售',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁、版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table if exists stock_order;
CREATE TABLE `stock_order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `sid` int(11) NOT NULL COMMENT '库存id',
  `name` varchar(30) NOT NULL DEFAULT '' COMMENT '商品名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

向 商品 `库存表` 中添加几条数据

```mysql
INSERT INTO `ms`.`stock` (`id`, `name`, `count`, `sale`, `version`) VALUES (1, 'iPhone6', 100, 0, 0);
INSERT INTO `ms`.`stock` (`id`, `name`, `count`, `sale`, `version`) VALUES (2, 'IPhone13', 100, 0, 0);
```

此时，数据库搭建完毕

### 2.2 搭建开发环境

此系统是基于Spring boot 、 Mybais Plus 和 Redis 开发的。

#### 2.2.1 创建一个Spring boot项目

![image-20220116140050592](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-16/0b4287ff0f32317129ad38107b7277d0.jpeg)

#### 2.2.2 添加其他依赖

打开 `pom.xml` 文件，添加上 `Mybatis Plus` 依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.4.3.4</version>
</dependency>
```

添加 `druid`依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.2.8</version>
</dependency>
```

**完整的pom文件**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.2</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.lyl</groupId>
    <artifactId>ms</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>ms</name>
    <description>秒杀项目</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <!--fastjson-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.79</version>
        </dependency>

        <!--hutool-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.7.16</version>
        </dependency>

        <!--druid-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.2.8</version>
        </dependency>

        <!--mybatis-plus-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.3.4</version>
        </dependency>

        <!--redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!--web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--devtools-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!--test-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

#### 2.2.3 编写`yml`配置文件

打开 `resources` 目录，选中 `application.properties` 修改文件的后缀为：`yml`

将下面内容拷贝到 `application.yml`中（==简单配置==）

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ms
    username: root
    password: root
    type: com.alibaba.druid.pool.DruidDataSource
mybatis-plus:
  mapper-locations: classpath*:/mapper/*.xml
  type-aliases-package: com.lyl.ms.entity
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  #开启日志
```

#### 2.2.4  生成代码

使用`mybatis-plus` 的代码生成器或者使用 `EasyCode插件` 来生成代码

生成代码之后，目录结构为：

```reStructuredText
D:\PROJECT\JAVASTUDYEXAMPLE\MS\SRC
├─main
│  ├─java
│  │  └─com
│  │      └─lyl
│  │          └─ms
│  │              │  MsApplication.java      //启动类
│  │              │
│  │              ├─controller               // 控制器
│  │              ├─dao                      // dao
│  │              │      StockDao.java       // 商品库存dao
│  │              │      StockOrderDao.java  // 商品订单dao
│  │              │
│  │              ├─entity                   // 实体类
│  │              │      Stock.java          // 商品实体
│  │              │      StockOrder.java     // 订单实体
│  │              │
│  │              └─service                          // service
│  │                  │  StockOrderService.java      // 订单业务接口
│  │                  │  StockService.java           // 商品库存业务接口
│  │                  │
│  │                  └─impl                               // 实现类
│  │                          StockOrderServiceImpl.java   // 订单业务实现类
│  │                          StockServiceImpl.java        // 商品库存实业务现类
│  │
│  └─resources  //配置文件目录
│      │  application.yml  //配置文件
│      │
│      ├─mapper //mybatis-plus 映射文件
│      │      StockDao.xml        // 商品库存操作
│      │      StockOrderDao.xml   // 订单操作
│      │
│      ├─static                   //静态资源文件夹
│      └─templates                // 模板
└─test                            // 测试类
    └─java
        └─com
            └─lyl
                └─ms
                        MsApplicationTests.java

```

---

##### 2.2.4.1 **实体类**

- 商品库存实体

```java
package com.lyl.ms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * (Stock)表实体类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Stock implements Serializable{
    private static final long serialVersionUID = -17172133476317770L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id; 

    /**
     * 名称
     */
    private String name; 

    /**
     * 库存
     */
    private Integer count; 

    /**
     * 已售
     */
    private Integer sale; 

    /**
     * 乐观锁、版本号
     */
    private Integer version; 

}
```

- 订单实体类

```java
package com.lyl.ms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * (StockOrder)表实体类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class StockOrder implements Serializable{
    private static final long serialVersionUID = 769803661818838703L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id; 

    /**
     * 库存id
     */
    private Integer sid; 

    /**
     * 商品名称
     */
    private String name; 

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String create_time; 

}
```



---

##### 2.2.4.2 持久层代码

> 如果不想每个接口都写 @Mapper，@Repository 这两个注解，可以在启动类上面添加  **==@MapperScan("com.lyl.ms.dao")==**

- **StockDao**

```java
package com.lyl.ms.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyl.ms.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (Stock)表数据库访问层
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Mapper
@Repository
public interface StockDao extends BaseMapper<Stock> {
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Stock> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Stock> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Stock> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Stock> entities);
}
```

**StockDao.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lyl.ms.dao.StockDao">
    
    <sql id="insert_column">
        name, count, sale, version    
    </sql>
    
    <sql id="all_column">
        id ,<include refid="insert_column"/>
    </sql>
    
    <resultMap type="com.lyl.ms.entity.Stock" id="StockMap">
        <result property="id" column="id"/>
        <result property="name" column="name"/>
        <result property="count" column="count"/>
        <result property="sale" column="sale"/>
        <result property="version" column="version"/>
    </resultMap>

    <!-- 批量插入 -->
    <insert id="insertBatch" keyProperty="id" useGeneratedKeys="true">
        insert into ms.stock(<include refid="insert_column"/>)
        values
        <foreach collection="entities" item="entity" separator=",">
            (#{entity.name}, #{entity.count}, #{entity.sale}, #{entity.version})
        </foreach>
    </insert>
    <!-- 批量插入或按主键更新 -->
    <insert id="insertOrUpdateBatch" keyProperty="id" useGeneratedKeys="true">
        insert into ms.stock(<include refid="insert_column"/>)
        values
        <foreach collection="entities" item="entity" separator=",">
            (#{entity.name}, #{entity.count}, #{entity.sale}, #{entity.version})
        </foreach>
        on duplicate key update
         name = values(name) , count = values(count) , sale = values(sale) , version = values(version)          
    </insert>

</mapper>
```



---

- **StockOrder**

```java
package com.lyl.ms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * (StockOrder)表实体类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class StockOrder extends Model<StockOrder> implements Serializable{
    private static final long serialVersionUID = 769803661818838703L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id; 

    /**
     * 库存id
     */
    private Integer sid; 

    /**
     * 商品名称
     */
    private String name; 

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String create_time; 

}
```

**StockOrderDao.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lyl.ms.dao.StockOrderDao">
    
    <sql id="insert_column">
        sid, name, create_time    
    </sql>
    
    <sql id="all_column">
        id ,<include refid="insert_column"/>
    </sql>
    
    <resultMap type="com.lyl.ms.entity.StockOrder" id="StockOrderMap">
        <result property="id" column="id"/>
        <result property="sid" column="sid"/>
        <result property="name" column="name"/>
        <result property="create_time" column="create_time"/>
    </resultMap>

    <!-- 批量插入 -->
    <insert id="insertBatch" keyProperty="id" useGeneratedKeys="true">
        insert into ms.stock_order(<include refid="insert_column"/>)
        values
        <foreach collection="entities" item="entity" separator=",">
            (#{entity.sid}, #{entity.name}, #{entity.createTime})
        </foreach>
    </insert>
    <!-- 批量插入或按主键更新 -->
    <insert id="insertOrUpdateBatch" keyProperty="id" useGeneratedKeys="true">
        insert into ms.stock_order(<include refid="insert_column"/>)
        values
        <foreach collection="entities" item="entity" separator=",">
            (#{entity.sid}, #{entity.name}, #{entity.createTime})
        </foreach>
        on duplicate key update
         sid = values(sid) , name = values(name) , create_time = values(create_time)          
    </insert>

</mapper>
```



---

##### 2.2.4.3 业务层代码

- **StockService**

```java
package com.lyl.ms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyl.ms.entity.Stock;

/**
 * (Stock)表服务接口
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
public interface StockService extends IService<Stock> {
    
}
```

**StockServiceImpl**

```java
package com.lyl.ms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.ms.dao.StockDao;
import com.lyl.ms.entity.Stock;
import com.lyl.ms.service.StockService;
import org.springframework.stereotype.Service;

/**
 * (Stock)表服务实现类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Service("stockService")
public class StockServiceImpl extends ServiceImpl<StockDao, Stock> implements StockService {

}
```



---

- **StockOrderService**

```java
package com.lyl.ms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyl.ms.entity.StockOrder;

/**
 * (StockOrder)表服务接口
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
public interface StockOrderService extends IService<StockOrder> {
    
}
```

**StockOrderServiceImpl**

```java
package com.lyl.ms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.ms.dao.StockOrderDao;
import com.lyl.ms.entity.StockOrder;
import com.lyl.ms.service.StockOrderService;
import org.springframework.stereotype.Service;

/**
 * (StockOrder)表服务实现类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Service("stockOrderService")
public class StockOrderServiceImpl extends ServiceImpl<StockOrderDao, StockOrder> implements StockOrderService {

}
```



##### 2.2.4.4 测试

在项目的 `test` 文件夹内，使用创建项目是创建的测试类，测试mybatis-plus

```java
@Autowired
private StockDao stockDao;

@Test
void testStockDao() {
    stockDao.selectList(null)
            .forEach(System.out::println);
}
```

如果可以查询出数据，证明项目没问题，可以继续下面的操作了

<img src="https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-16/3a16e9867c9be6fc932f684a7add1ed7.jpeg" alt="image-20220116151124244" style="zoom:80%;" />

### 2.3 编写秒杀方法

==**梳理业务逻辑**==

- 用户秒杀的时候，传递商品的id给服务器
- 服务器先校验商品的库存是否充足
  - 如果商品的库存充足
    - 扣除库存
    - 生成订单
    - 返回生成的订单信息
  - 如果商品的库存不足
    - 直接返回秒杀失败

**编写代码**

在 `controller` 包中新建一个 `MsController` 控制器

```java
package com.lyl.ms.controller;

import com.lyl.ms.service.MsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:17
 */
@RestController
@RequestMapping("/ms")
@RequiredArgsConstructor
public class MsController {
    private final MsService msService;


    @GetMapping("/kill")
    public String kill(Integer id){
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.kill(id);
    }

}
```

**在 service 包中创建 MsService 接口**

```java
package com.lyl.ms.service;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:24
 */
public interface MsService {

    /**
     * 秒杀方法
     * @param id 商品id
     * @return 提示信息
     */
    String kill(Integer id);
}
```

**创建接口的实现类**

```java
package com.lyl.ms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lyl.ms.common.exeception.MyException;
import com.lyl.ms.dao.StockDao;
import com.lyl.ms.entity.Stock;
import com.lyl.ms.entity.StockOrder;
import com.lyl.ms.service.MsService;
import com.lyl.ms.service.StockOrderService;
import com.lyl.ms.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MsServiceImpl implements MsService {
    private final StockDao stockDao;
    private final StockOrderService orderService;


    @Override
    public String kill(Integer id) {
        //校验库存
        Stock stock = checkStock(id);
        //扣除库存
        stock.setSale(stock.getSale() + 1);
        stockDao.updateById(stock);
        //生成订单
        StockOrder order = new StockOrder();
        order.setSid(stock.getId())
                .setName(stock.getName());
        orderService.save(order);
        log.info("秒杀成功,生成的订单号为：{}" , order.getId());
        return "秒杀成功,生成的订单号为：" + order.getId();
    }

    /**
     * 校验库存
     * @param id 商品id
     * @return 商品
     */
    private Stock checkStock(Integer id) {
        Stock stock = stockDao.selectById(id);
        if (stock == null){
            throw new MyException("秒杀商品 id=" + id + "不存在");
        }
        if (stock.getSale() - stock.getCount() >= 0){
            throw new MyException("秒杀失败:  库存不足");
        }
        return stock;
    }
}
```



> 其中使用到了自定义异常，和全局异常处理

**异常类**

```java
package com.lyl.ms.common.exeception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:46
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MyException extends RuntimeException{

    private String msg;

}
```

**全局异常处理**

```java
package com.lyl.ms.common.config;

import com.lyl.ms.common.exeception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:44
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionConfig {

    /**
     * 处理自定义异常
     * @param e 自定义异常
     * @return msg
     */
    @ExceptionHandler(MyException.class)
    public String onMyException(MyException e) {
        log.error("自定义异常", e.getMsg());
        return e.getMsg();
    }

    /**
     * 处理其他异常
     * @param e 异常
     * @return msg
     */
    @ExceptionHandler(Exception.class)
    public String onException(Exception e){
        log.error("全局异常捕获", e.getMessage());
        return e.getMessage();
    }
}
```

### 2.4 测试接口

#### 2.4.1 普通测试

1. 启动项目

2. 打开 `postman` 测试接口

![image-20220117153641674](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/87dec2ceda3802eb00cbb475c5f33b17.jpeg)

> 图中的 `{{local}}` 为 `postman` 中的全局变量，

<img src="https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/f9f95df371dd98e1d8513edfbec0d177.jpeg" alt="image-20220117154035877" style="zoom: 50%;" />

#### 2.4.2 并发测试

使用 `Jmeter` 进行并发测试

1. 创建执行计划

准备150个线程，同时操作秒杀接口

![image-20220117160653043](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/4cc8d82714d8282b9170b5c8a9f9eb2c.jpeg)

![image-20220117160610124](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/52f6fb32c9521f177492a090ddadd1b8.jpeg)

**查看数据库原始数据**

- 商品的原始库存100个

![image-20220117160910830](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/6bc300ce4db95afb2b6fc5f602bfd6c9.jpeg)

**启动 JMeter测试**

![image-20220117161013307](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/225e52b3120e6cb77242ff98eeece949.jpeg)

**销售情况**

![image-20220117161150769](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/c585ea6851d3ddb7082e80bd6096e68f.jpeg)

此时，产生了150个销售额，也就意味着着商商品超卖了。

### 2.5 测试sql

> 查看秒杀接口执行的sql情况

```mysql
-- 查看商品的销售情况
SELECT
	s.id AS 商品编号,
	s.NAME AS 商品名称,
	s.count as 商品总量,
	s.sale AS 商品销量,
	count( t.id ) AS 订单数量 
FROM
	stock s
	JOIN stock_order t ON s.id = t.sid 
WHERE
	s.id = 1;
	
-- 测试环境恢复
TRUNCATE TABLE stock_order;
ALTER TABLE stock_order auto_increment = 1;
update stock set sale = 0 , version = 0 where id = 1;
```

## 3. 防止超卖

对于商品超卖的问题有几种解决方案

- 使用悲观锁
- 使用乐观锁
- ……

### 3.1 使用悲观锁

> 直接在方法上面添加上 `synchronized` 关键字

**代码如下:**

```java
@Override
public synchronized String kill(Integer id) {
    //校验库存
    Stock stock = checkStock(id);
    //扣除库存
    int sale = stockDao.sale(stock);
    //生成订单
    StockOrder order = new StockOrder();
    order.setSid(stock.getId())
            .setName(stock.getName());
    orderService.save(order);
    log.info("秒杀成功,生成的订单号为：{}" , order.getId());
    return "秒杀成功,生成的订单号为：" + order.getId();
}
```

**再次测试**

![image-20220117164221391](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/6170dd32bedb0fb79d888a0a2b654052.jpeg)

![image-20220117164244459](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-17/ee89cf29df511a2382a07a2fcb8c091c.jpeg)

此时，商品不会出现超卖的问题，但是如果碰到出现几百万，上千万的并发调用 秒杀接口，可能会造成服务器的宕机。

所以，此种方法不推荐

### 3.2 使用乐观锁

> 关键点：使用乐观锁更新失败后，直接抛弃请求

在 `config` 包下新建 `MpConfig.java` 配置类，配置 `mybatis-plus乐观锁插件` ，然后修改 `商品实体类` 在 `version` 字段上面添加上 `@Version` 注解

```java
package com.lyl.ms.common.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 罗亚龙
 * @date 2022/1/19 14:06
 */
@Configuration
public class MpConfig {

    /**
     * 乐观锁插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

}
```

修改`Stock实体类`

```java
/**
 * 乐观锁、版本号
 */
@Version
private Integer version; 
```

---

为了区别悲观锁，我们重新创建一个接口

**MsController**

```java
/**
 * 乐观锁版本的秒杀
 * @param id 商品id
 * @return str
 */
@GetMapping("/killByOptimismLock")
public String killByOptimismLock(Integer id){
    if (id == null){
        //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
        return "商品id不能为空";
    }
    return msService.killByOptimismLock(id);
}
```

**ServiceImpl**

```java
@Override
public String killByOptimismLock(Integer id) {
    //校验库存
    Stock stock = checkStock(id);
    //扣除库存
    stock.setSale(stock.getSale() + 1);
    int i = stockDao.updateById(stock);
    if (i == 0){
        log.error("秒杀失败: 乐观锁更新失败");
        return "秒杀失败: 乐观锁更新失败";
    }

    //生成订单
    StockOrder order = new StockOrder();
    order.setSid(stock.getId())
            .setName(stock.getName());
    orderService.save(order);
    log.info("秒杀成功,生成的订单号为：{}" , order.getId());
    return "秒杀成功,生成的订单号为：" + order.getId();
}
```

#### 3.2.1 开始测试

向之前一样，新建一个线程组，开始测试

![image-20220119151236881](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-19/592636198a6f16cf47d3caedf4dab10f.jpeg)

**控制台信息**

![image-20220119151453731](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-19/57ba53036216974f650e883c3243f9d1.jpeg)

**通过sql查看数据库中的数据**

```mysql
-- 查看商品的销售情况
SELECT
	s.id AS 商品编号,
	s.NAME AS 商品名称,
	s.count as 商品总量,
	s.sale AS 商品销量,
	count( t.id ) AS 订单数量 
FROM
	stock s
	JOIN stock_order t ON s.id = t.sid 
WHERE
	s.id = 1;
```

**销售情况**

![image-20220119151629174](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-19/d547f60f6ac5a14836e4bd064f299ca9.jpeg)

## 4 接口限流

如果接口没有做限流保护，可能会被不法分子，编写一个脚本，一直请求接口，导致正常的接口进不来，同时也有可能接口请求过多，造成服务器的宕机，因此我们需要对接口做一下限流保护

### 4.1 常用的限流算法

> 常用的限流算法有两种：漏桶算法和令牌桶算法

1. **漏桶算法**

   漏桶算法的思路很简单：漏桶算法思路很简单，水（请求）先进入到漏桶里，漏桶以一定的速度出水，当水流入速度过大会直接溢出，可以看出漏桶算法能强行限制数据的传输速率。

![img](https://images0.cnblogs.com/blog/522490/201411/081225378155003.png)

2. 令牌桶算法

   对于很多应用场景来说，除了要求能够限制数据的平均传输速率外，还要求允许某种程度的突发传输。这时候漏桶算法可能就不合适了，令牌桶算法更为适合。如图2所示，令牌桶算法的原理是系统会以一个恒定的速度往桶里放入令牌，而如果请求需要被处理，则需要先从桶里获取一个令牌，当桶里没有令牌可取时，则拒绝服务。

   ![img](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-19/eeb207ec1c1b8ff7f60818b4f3e648a3.jpeg)

   

### 4.2 添加依赖

**本次我们使用令牌桶来做限流**

使用 `Google` 的 `guava` 中的 `RateLimiter` 来做限流

> **注意：** 此种方法可能导致商品没有秒杀完全（也就是说，还有待出售的商品）

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
    <scope>compile</scope>
</dependency>
```

限流一般是放在控制器内

```java
//创建令牌桶实例
RateLimiter limiter = RateLimiter.create(10);
//代表每秒产生10个令牌

//使用

//没有获取令牌的请求，会一直等待获取令牌
limiter.acquire();

//2. 设置等待时间，如果在等待时间内获取到了令牌，则处理业务。否则，抛弃
limiter.tryAcquire(2,TimeUnit.SECOND);
```

### 4.3 基本使用

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

1. 超时抛弃

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

### 4.4 修改秒杀接口

```java
//创建实例：每秒种产生20个令牌
private RateLimiter limiter = RateLimiter.create(20);

/**
 * 令牌桶 + 乐观锁版本的秒杀
 * @param id 商品id
 * @return str
 */
@GetMapping("/killByOptimismLockLimit")
public String killByOptimismLockLimit(Integer id){
    //请求在2秒钟内没有获取到令牌，会直接抛弃请求
    if (!limiter.tryAcquire(2, TimeUnit.SECONDS)){
        log.error("抢购失败：服务器进行了限流");
    }
    if (id == null){
        //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
        return "商品id不能为空";
    }
    return msService.killByOptimismLock(id);
}
```

## 5. 秒杀接口限时

​	目前已经完成了防止商品超卖和接口限流，已经能够防止大流量把我们的服务器直接搞炸。我们需要关心一些细节问题。

1. 我们应该在一定时间内执行秒杀处理，不能在任意时间都接受秒杀请求，如果加入时间验证？
2. 对于稍微懂点电脑的人来说，又会动用歪脑筋通过抓包的方式来获取我们的接口地址。然后通过脚本来进行抢购怎么办？
3. 秒杀开始之后如何限制单个用户的请求频率，即单位时间内限制访问次数？

**主要内容**

- 限时抢购
- 抢购接口隐藏
- 单用户限制频率（单位时间内限制访问次数）

### 5.1 限时抢购的实现

​	使用`redis`来记录秒杀商品的时间，对秒杀过期的请求进行过期处理

**操作过程**

- 在秒杀活动开始之前，提前将秒杀商品添加到 `redis` 中，并且设置过期时间
- 秒杀的时候，前置判断 `redis` 中是否还存在秒杀商品，如果不存在，则秒杀活动已经就结束了

#### 5.1.1 添加 `redis` 

1. 在`pom.xml`文件中添加依赖

```xml
<!--redis-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2. 编辑 `appplication.yml` 配置文件

   ```yaml
   spring:
     datasource:
       driver-class-name: com.mysql.cj.jdbc.Driver
       url: jdbc:mysql://121.89.199.231:3306/ms
       username: root
       password: Lyl123456
       type: com.alibaba.druid.pool.DruidDataSource
     redis:  #redis配置
       host: localhost
       password: 123456
   mybatis-plus:
     mapper-locations: classpath*:/mapper/*.xml
     type-aliases-package: com.lyl.ms.entity
   #  configuration:
   #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  #mybatis-plus日志
   ```

3. 创建 `redis` 配置类

   ```java
   package com.lyl.ms.common.config;
   
   import com.fasterxml.jackson.annotation.JsonAutoDetect;
   import com.fasterxml.jackson.annotation.PropertyAccessor;
   import com.fasterxml.jackson.databind.ObjectMapper;
   import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
   import org.springframework.cache.CacheManager;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.data.redis.cache.RedisCacheConfiguration;
   import org.springframework.data.redis.cache.RedisCacheManager;
   import org.springframework.data.redis.connection.RedisConnectionFactory;
   import org.springframework.data.redis.core.RedisTemplate;
   import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
   import org.springframework.data.redis.serializer.RedisSerializationContext;
   import org.springframework.data.redis.serializer.RedisSerializer;
   import org.springframework.data.redis.serializer.StringRedisSerializer;
   
   /**
    * @author 罗亚龙
    * @date 2022/1/19 16:38
    */
   @Configuration
   public class RedisConfig {
       @Bean
       public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
           RedisTemplate<String, Object> template = new RedisTemplate();
           template.setConnectionFactory(factory);
   
           //json序列化方式
           Jackson2JsonRedisSerializer valueSerializer = getValueSerializer();
   
           StringRedisSerializer keySerializer = new StringRedisSerializer();
   
           template.setKeySerializer(keySerializer);
           template.setValueSerializer(valueSerializer);
   
           template.setHashKeySerializer(keySerializer);
           template.setHashValueSerializer(valueSerializer);
   
           //将所有配置set进配置文件中
           template.afterPropertiesSet();
           return template;
       }
   
       /**
        * value值的序列化
        * @return
        */
       private Jackson2JsonRedisSerializer getValueSerializer() {
           Jackson2JsonRedisSerializer valueSerializer = new Jackson2JsonRedisSerializer(Object.class);
           ObjectMapper om = new ObjectMapper();
           om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
           om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL);
           valueSerializer.setObjectMapper(om);
           return valueSerializer;
       }
   
       //如果使用缓存注解会使用到的配置
       @Bean
       public CacheManager cacheManager(RedisConnectionFactory factory) {
           RedisSerializer<String> redisSerializer = new StringRedisSerializer();
           Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = getValueSerializer();
   
           // 配置序列化
           RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
           RedisCacheConfiguration redisCacheConfiguration = config
                   .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                   .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
   
           RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                   .cacheDefaults(redisCacheConfiguration)
                   .build();
           return cacheManager;
       }
   }
   ```

#### 5.1.2 新增限时接口

在控制器中添加新接口

```java
/**
 * 令牌桶 + 乐观锁 + 限时 版本的秒杀
 * @param id 商品id
 * @return str
 */
@GetMapping("/killByTimeLimit")
public String killByTimeLimit(Integer id){
    //请求在2秒钟内没有获取到令牌，会直接抛弃请求
    if (!limiter.tryAcquire(2, TimeUnit.SECONDS)){
        log.error("抢购失败：服务器进行了限流");
    }
    if (id == null){
        //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
        return "商品id不能为空";
    }
    return msService.killByTimeLimit(id);
}
```

**ServiceIMpl**

```java
@Autowired
private RedisTemplate<String,Object> redisTemplate;

@Override
public String killByTimeLimit(Integer id) {
    if (!redisTemplate.hasKey("stock:" + id)) {
        log.error("秒杀失败: 秒杀活动已经结束了");
        return "秒杀失败: 秒杀活动已经结束了";
    }
    //校验库存
    Stock stock = checkStock(id);
    //扣除库存
    stock.setSale(stock.getSale() + 1);
    int i = stockDao.updateById(stock);
    if (i == 0){
        log.error("秒杀失败: 乐观锁更新失败");
        return "秒杀失败: 乐观锁更新失败";
    }

    //生成订单
    StockOrder order = new StockOrder();
    order.setSid(stock.getId())
            .setName(stock.getName());
    orderService.save(order);
    log.info("秒杀成功,生成的订单号为：{}" , order.getId());
    return "秒杀成功,生成的订单号为：" + order.getId();
}
```

#### 5.1.3 新增设置秒杀商品接口

**控制器**

```java
/**
 * 设置秒杀商品和过期时长
 * @param id 秒杀商品id
 * @param time 过期时长(秒)
 * @return
 */
@GetMapping("/setKillStockTime")
public String setKillStockTime(Integer id,long time){
    //参数校验
    return msService.setKillStockTime(id,time);
}
```

**ServiceImpl**

```java
@Override
public String setKillStockTime(Integer id, long time) {
    redisTemplate.opsForValue().set("stock:" + id,true,time, TimeUnit.SECONDS);
    return "成功设置商品的秒杀时间";
}
```

#### 5.1.4 测试

1. 调用 `setKillStockTime` 接口，向 `redis` 中放入秒杀商品，并设置过期时间
2. 使用 `Jmeter` 进行测试

**结果如下**

![``](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-20/637b908ceca476ac07932712eb8a79e7.jpeg)

![image-20220120145411859](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-20/e398a71a043bd207a5addf2c0828d565.jpeg)

## 6. 接口隐藏

如果我们的接口没有做任何保护措施，任何人都可以来访问，那对于我们的服务器来说，很不安全，很容易被不法分子攻击，造成服务器宕机。因此我们需要对接口进行隐藏，只有合法的用户才可以进行商品的秒杀

**方案**

- 我们在调用秒杀接口前，获取一个token，并且存入 `redis` 中，设置过期时间
- 调用接口的时候，传入用户id 和 token，在调用秒杀请求的时候，校验请求的合法性

### 6.1 获取token接口

**逻辑**

- 我们需要获取 `商品id` 和 `用户id` 根据当前时间戳 ，生成token
- 生成 `token` 的时候，加盐
- 生成 `token` 之后，设置过期时间，放入 `redis` 中

**代码实现**

**获取token的接口**

```java
/**
 * 获取token
 * @param id 商品id
 * @param userid 用户id
 * @return token
 */
@GetMapping("/getToken")
public String getToken(Integer id,Integer userid){
    return msService.getToken(id,userid);
}
```

**业务逻辑**

```java
@Override
public String getToken(Integer id, Integer userid) {
    //校验商品是否存在，是否参与秒杀
    //校验用户是否存在，是否有秒杀的资格

    //生成token 加盐
    String salt = "#@$%#@!";

    //生成token
    String token = SecureUtil.md5(System.currentTimeMillis() + "_" + userid + "_" + salt);
    String key = "ms:token:" + id + ":" + userid;
    //放在redis中，并设置过期时间
    redisTemplate.opsForValue().set(key, token, 5 * 60, TimeUnit.SECONDS);
    return token;
}
```

**发起请求，生成token**

![image-20220120161119761](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-20/a727b94de41e076cb691ea78e7554904.jpeg)

### 6.2 修改秒杀接口

秒杀接口需要增加两个参数：`userid` 和 `token` ，用来校验token

如果token校验通过，则执行秒杀。

**代码实现**

**接口**

```java
/**
 * 秒杀接口（需要携带token）
 * @param id 商品id
 * @param userid 用户id
 * @param token token
 * @return
 */
@GetMapping("/killByToken")
public String killByToken(@RequestParam Integer id,
                          @RequestParam Integer userid,
                          @RequestParam String token){
    //参数校验
    //请求在2秒钟内没有获取到令牌，会直接抛弃请求
    if (!limiter.tryAcquire(2, TimeUnit.SECONDS)){
        log.error("抢购失败：服务器进行了限流");
    }
    if (id == null){
        //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
        return "商品id不能为空";
    }
    return msService.killByToken(id,userid,token);
}
```

**业务实现**

```java
@Override
public String killByToken(Integer id, Integer userid, String token) {
    //校验请求的合法性
    String key = "ms:token:" + id + ":" + userid;
    String oldToken = (String) redisTemplate.opsForValue().get(key);
    if (StrUtil.isBlank(oldToken)
            || (StrUtil.isNotBlank(oldToken) && !oldToken.equals(token))) {
        log.error("请求不合法或者token过期");
        return "请求不合法或者token过期";
    }


    if (!redisTemplate.hasKey("stock:" + id)) {
        log.error("秒杀失败: 秒杀活动已经结束了");
        return "秒杀失败: 秒杀活动已经结束了";
    }
    //校验库存
    Stock stock = checkStock(id);
    //扣除库存
    stock.setSale(stock.getSale() + 1);
    int i = stockDao.updateById(stock);
    if (i == 0) {
        log.error("秒杀失败: 乐观锁更新失败");
        return "秒杀失败: 乐观锁更新失败";
    }

    //生成订单
    StockOrder order = new StockOrder();
    order.setSid(stock.getId())
            .setName(stock.getName());
    orderService.save(order);
    log.info("秒杀成功,生成的订单号为：{}", order.getId());
    return "秒杀成功,生成的订单号为：" + order.getId();
}
```

### 6.3 测试

在测试之前，需要做一些准备工作

1. 使用以下 `sql` 语句，来恢复数据库中的数据

```mysql
-- 测试环境恢复
TRUNCATE TABLE stock_order;
ALTER TABLE stock_order auto_increment = 1;
update stock set sale = 0 , version = 0 where id = 1;
```

2. 调用 `setKillStockTime` 接口，设置秒杀商品
3. 调用 `getToken` 获取合法的token
4. 编写 `Jmeter` 测试脚本

准备工作做好之后，启动 `JMeter` 来执行秒杀，执行结果如下

**控制台**

![image-20220120162029009](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-20/7948ec93c4b8540209bd3ce68529802d.jpeg)

**查看销售情况**

```mysql
-- 查看商品的销售情况
SELECT
	s.id AS 商品编号,
	s.NAME AS 商品名称,
	s.count as 商品总量,
	s.sale AS 商品销量,
	count( t.id ) AS 订单数量 
FROM
	stock s
	JOIN stock_order t ON s.id = t.sid 
WHERE
	s.id = 1;
```

![image-20220120162147420](https://gitee.com/luoyalongLYL/upload_image_repo/raw/master/typroa/2022-01-20/c11e03713d9f50b47af1585f34e3878d.jpeg)

测试成功

## 7. 后语

​		接口秒杀还有很多的知识点，以上仅仅是用作学习用，源码和笔记会放在 GitHub上面，如果看过之后，觉得OK的话，别忘了 `star` 哦。






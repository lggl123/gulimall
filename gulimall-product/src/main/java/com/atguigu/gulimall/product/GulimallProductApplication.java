package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1、整合MyBatis-Plus
 *      1）、导入依赖
 *      <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.2.0</version>
 *      </dependency>
 *      2）、配置
 *          1、配置数据源；
 *              1）、导入数据库的驱动。https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-versions.html
 *              2）、在application.yml配置数据源相关信息
 *          2、配置MyBatis-Plus；
 *              1）、使用@MapperScan
 *              2）、告诉MyBatis-Plus，sql映射文件位置
 *
 * 2、逻辑删除
 *  1）、配置全局的逻辑删除规则（省略）
 *  2）、配置逻辑删除的组件Bean（省略）
 *  3）、给Bean加上逻辑删除注解@TableLogic
 *
 * 3、JSR303
 *   1）、给Bean添加校验注解:javax.validation.constraints，并定义自己的message提示
 *   2)、开启校验功能@Valid
 *      效果：校验错误以后会有默认的响应；
 *   3）、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果
 *   4）、分组校验（多场景的复杂校验）
 *         1)、	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
 *          给校验注解标注什么情况需要进行校验
 *         2）、@Validated({AddGroup.class})
 *         3)、默认没有指定分组的校验注解@NotBlank，在分组校验情况@Validated({AddGroup.class})下不生效，只会在@Validated生效；
 *
 *   5）、自定义校验
 *      1）、编写一个自定义的校验注解
 *      2）、编写一个自定义的校验器 ConstraintValidator
 *      3）、关联自定义的校验器和自定义的校验注解
         *      @Documented
         * @Constraint(validatedBy = { ListValueConstraintValidator.class【可以指定多个不同的校验器，适配不同类型的校验】 })
         * @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
         * @Retention(RUNTIME)
         * public @interface ListValue {
 *
 * 4、统一的异常处理
 * @ControllerAdvice
 *  1）、编写异常处理类，使用@ControllerAdvice。
 *  2）、使用@ExceptionHandler标注方法可以处理的异常。
 *  5，模板引擎
 *  （1）thymeleaf-starter: 关闭缓存
 *  （2）静态资源都放在static文件夹下就可以直接按照路径访问
 *  （3）页面放在templates下，直接访问
 *          springboot，访问项目的时候，默认会找index
 *   6，整合redis
 *   （1）引入data-redis-stater
 *   （2）简单配置redis的host等信息
 *   （3）使用springboot自动配好的StringRdedisTemplate来操作redis
 *   7,整合redisson作为分布式锁等功能框架
 *      (1)引入依赖
 *      （2）配置redisson
 *   8,整合SpringCache简化缓存开发
 *   （1）引入cache依赖和redis依赖
 *   (2) 写配置
 *          自动配置了哪些
 *              CacheAutoConfigration会导入 RedisCacheConfigration
 *              自动配好了缓存管理器
 *          配置使用redis作为缓存
 *          测试使用缓存
 *          1,开启缓存功能
 *          2,只需要注解就能完成缓存操作
 *          3,默认行为
 *              如果缓存中村子啊，方法不用调用
 *              key是默认自动生成的：缓存名字：
 *              缓存的value值，默认使用jdk序列化机制，讲序列化后的数据存到redis
 *              默认时间 -1
 *
 *          自定义操作
 *              指定生成的缓存使用的key
 *              指定缓存数据的存活时间
 *              将数据保存为json格式
 *
 *                  CacheAutoConfiguration导入了RedisCacheConfiguration->自动配置了缓存管理器RedisCacheManager
 *                  ->初始化所有的缓存->每个缓存决定使用什么配置->如果使用RedisCacheConfiguration有就用已有的，没有就用默认配置
 *                  ->想改缓存配置，只需要给容器中放一个RedisCacheConfiguration即可
 *                  ->就会应用到当前缓存管理器RedisCacheManager管理的所有缓存分区中
 */
@EnableCaching   //开启缓存注解
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}

package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {
    /**
     * 所有对redisson的使用都是通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException{
        //创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://101.43.11.26:6379").setDatabase(12).setPassword("z4upkpaeby");
        //根据Config创建出RedissonClient实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}

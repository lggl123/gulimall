package com.atguigu.gulimall.product.web;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //TODO 1,查出所有的一级分类
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1，获取一把锁
        RLock lock = redissonClient.getLock("my-lock");
        //2，加锁
//        lock.lock();   //阻塞式等待  默认加的锁都是30s时间
        //如果没有指定锁的超时时间，就使用看门狗默认时间30秒
        //（1）锁的自动续期，如果业务超长，运行期间自动给锁续上新的时间周期。不用担心业务时间长，锁自动过期被删掉
        //（2）加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁也会默认在30秒以后自动删除

        lock.lock(10, TimeUnit.SECONDS);  //10秒钟自动解锁,自动解锁时间一定要大于业务执行时间
        //锁时间到了以后不会自动续期
        //如果传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        //只要占锁成功，就会启动一个定时任务【重新给锁设置一个过期时间,新的过期时间就是看门狗的默认时间】
        //三分之一的看门狗时间，续一个期
        try {

            System.out.println("加锁成功，"+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            //3，解锁
            lock.unlock();
            System.out.println("释放锁"+Thread.currentThread().getId());
        }
        return "hello";
    }


}

package com.hmdp;

import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private IShopService shopService;

    @Resource
    private RedisIdWorker redisIdWorker;

    private ExecutorService es= Executors.newFixedThreadPool(500);

    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task=()->{
            for (int i=0;i<100;i++){
                long id=redisIdWorker.nextId("order");
                System.out.println(id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i=0;i<300;i++){
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time="+(end-begin));
    }




    /**
     * 预热商铺缓存的测试方法
     */
    @Test
    void testSaveShop() {
        // 1. 根据 id 查询店铺数据
        Long id = 4L;
        Shop shop = shopService.getById(id);

        // 2. 将数据存入 Redis，并设置逻辑过期时间
        // 参数说明：Key前缀, 原始数据, 过期数值, 时间单位
        cacheClient.setWithLogicalExpire(
                RedisConstants.CACHE_SHOP_KEY + id,
                shop,
                10L,
                TimeUnit.SECONDS
        );

        System.out.println("预热完成！已存入商铺 ID: " + id);
    }
}
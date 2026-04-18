package com.hmdp;

import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private IShopService shopService;

    /**
     * 预热商铺缓存的测试方法
     */
    @Test
    void testSaveShop() {
        // 1. 根据 id 查询店铺数据
        Long id = 1L;
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
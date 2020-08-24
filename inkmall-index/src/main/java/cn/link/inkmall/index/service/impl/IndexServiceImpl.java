package cn.link.inkmall.index.service.impl;

import cn.link.inkmall.index.annotation.Cache;
import cn.link.inkmall.index.feign.PmsFeign;
import cn.link.inkmall.index.service.IndexService;
import cn.link.inkmall.pms.vo.CategoryVo;
import cn.link.inkmall.pms.entity.CategoryEntity;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Link
 * @Date: 2020/5/26 21:03
 * @Version 1.0
 */
@Service
public class IndexServiceImpl implements IndexService {

    private static final String KEY_PREFIX = "index:cates:";

    @Autowired
    PmsFeign pmsFeign;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public List<CategoryEntity> getLevelOneCategories() {

        return pmsFeign.getCategoryByParentId(0L).getData();
    }

    @Override
    @Cache(prefix = KEY_PREFIX, lock = "TypeLock", expire = 43200, random = 4320)
    public List<CategoryVo> getLevelTwoAndLevelThreeCategories(Long pid) {

        //使用 aop 环绕通知实现缓存与分布式锁，当前方法只需处理远程调用即可
        //空 远程调用，反序列化保存，返回
        List<CategoryVo> categoryVos = pmsFeign.getLevelTwoAndThreeByLevelOneId(pid).getData();
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryVos));
        return categoryVos;

    }


    /**
     * 手动实现分布式锁
     * @param pid
     * @return
     */
    public List<CategoryVo> cacheTest(Long pid) {

        //访问缓存
        String catesJsonStr = stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (!StringUtils.isEmpty(catesJsonStr)) {
            //缓存中存在数据，反序列化后返回
            return JSON.parseArray(catesJsonStr, CategoryVo.class);
        }

        //缓存中不存在数据
        //防止缓存击穿，使用分布式锁，每个分类有自己的锁，优化服务器访问效率
        String lock = "lock:" + pid;
        if (!StringUtils.isEmpty(stringRedisTemplate.opsForValue().get(lock))) {
            //已经加锁递归重试
            this.cacheTest(pid);
        }

        //没有锁就获取锁
        //加锁优化 - 1. 防止宕机后无法解锁,设置过期时间 2. 防止过期时间短于业务时间导致解锁其他请求的锁，设置唯一标识UUID
        String lockValue = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(lock, lockValue, 5, TimeUnit.SECONDS);

        //执行业务
        List<CategoryVo> categoryVos = pmsFeign.getLevelTwoAndThreeByLevelOneId(pid).getData();

        //设置结果到缓存
        if (CollectionUtils.isEmpty(categoryVos)) {
            //防止穿透，如果数据为空也存入，设定过期时间
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, "null", 3, TimeUnit.SECONDS);
        }else {
            //防止雪崩，数据不为空，给数据添加随机过期时间
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryVos), 43200 + new Random().nextInt(1440), TimeUnit.MINUTES);
        }

        //解锁   使用lua脚本进行 判断 + 解锁操作，保证原子性
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        stringRedisTemplate.execute(new DefaultRedisScript<>(), Arrays.asList("lock"), lockValue);

        return categoryVos;

    }

    /**
     * 使用redisson实现分布式锁
     * @param pid
     * @return
     */
    public List<CategoryVo> redissonCacheTest(Long pid) {

        //判断缓存中是否存在数据
        String categoryVosJsonStr = stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(categoryVosJsonStr)) {
            return JSON.parseArray(categoryVosJsonStr, CategoryVo.class);
        }

        //不存在数据，防止击穿，分布式锁
        String lock = "lock:" + pid;
        RLock fairLock = redissonClient.getFairLock(lock);
        fairLock.lock();

        //获取锁后判断，如果上个请求已经设置好了就直接返回
        if (StringUtils.isNotBlank(categoryVosJsonStr = stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid))) {
            fairLock.unlock();
            return JSON.parseArray(categoryVosJsonStr, CategoryVo.class);
        }

        //执行业务
        List<CategoryVo> categoryVos = pmsFeign.getLevelTwoAndThreeByLevelOneId(pid).getData();

         //设置结果到缓存
        if (CollectionUtils.isEmpty(categoryVos)) {
            //防止穿透，如果数据为空也存入，设定过期时间
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, "null", 3, TimeUnit.SECONDS);
        }else {
            //防止雪崩，数据不为空，给数据添加随机过期时间
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryVos), 43200 + new Random().nextInt(1440), TimeUnit.MINUTES);
        }

        fairLock.unlock();

        return categoryVos;
    }
}

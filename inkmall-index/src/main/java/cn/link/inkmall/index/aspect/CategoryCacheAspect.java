package cn.link.inkmall.index.aspect;

import cn.link.inkmall.index.annotation.Cache;
import cn.link.inkmall.pms.vo.CategoryVo;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 处理 category 的缓存的切面类
 * @Author: Link
 * @Date: 2020/5/31 10:33
 * @Version 1.0
 */
@Aspect
@Component
public class CategoryCacheAspect {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Around("@annotation(cn.link.inkmall.index.annotation.Cache)")
    public Object handleCategoryCache(ProceedingJoinPoint joinPoint) throws Throwable {

        //获取方法信息 -> 获取注解的信息
        //args 为集合 pid 转为字符串，样式为 "[x]"
        String args = Arrays.asList(joinPoint.getArgs()).toString();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> returnType = method.getReturnType();

        Cache annotation = method.getAnnotation(Cache.class);

        //查询缓存中是否存在
        String key = annotation.prefix() + args;
        String catesJsonStr = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(catesJsonStr)) {
            //存在就直接返回
            if (StringUtils.equals(catesJsonStr, "null")) {
                return null;
            }

            return JSON.parseObject(catesJsonStr, returnType);

        }

        //不存在，防止缓存击穿，加锁，远程调用查询
        //锁为 锁名 + pid ， 只有单个分类之间的查询才加锁
        RLock fairLock = redissonClient.getFairLock(annotation.lock() + args);
        fairLock.lock();

        //获取锁后再查一次，确认前面一条请求是否已将数据存入缓存中
        if (StringUtils.isNotBlank(catesJsonStr = stringRedisTemplate.opsForValue().get(key))) {
            //存在就解锁，返回
            fairLock.unlock();
            //null - 直接返回
            if (StringUtils.equals(catesJsonStr, "null")) {
                return null;
            }

            return JSON.parseObject(catesJsonStr, returnType);
        }

        //不存在就远程调用查询
        Object result = joinPoint.proceed(joinPoint.getArgs());

        if (result == null) {
            //为空，防止缓存击穿，设置短的过期时间存入(原子性)
            stringRedisTemplate.opsForValue().set(key, "null", 5, TimeUnit.SECONDS);
        } else {
            //不为空，反序列化为 JSON 存入缓存，防止缓存雪崩，设置过期时间
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(result), annotation.expire() + new Random().nextInt(annotation.random()), TimeUnit.MINUTES);
        }
        fairLock.unlock();


        return result;

    }

}

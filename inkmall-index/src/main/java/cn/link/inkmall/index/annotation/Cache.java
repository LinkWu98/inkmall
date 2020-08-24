package cn.link.inkmall.index.annotation;

/**
 * 修饰商品分类缓存的注解
 *
 * @Author: Link
 * @Date: 2020/5/31 10:24
 * @Version 1.0
 */
public @interface Cache {

    /**
     * 存redis中的数据的key前缀
     */
    String prefix();


    /**
     * redisson分布式锁名
     */
    String lock() default "lock";

    /**
     * 缓存过期时间，单位：分钟
     */
    int expire();


    /**
     * 防止雪崩的随机时间范围，单位：分钟
     *
     */
    int random();


}

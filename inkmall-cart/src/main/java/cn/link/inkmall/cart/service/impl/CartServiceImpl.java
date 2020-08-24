package cn.link.inkmall.cart.service.impl;

import cn.link.inkmall.cart.bean.CartItem;
import cn.link.inkmall.cart.bean.UserInfo;
import cn.link.inkmall.cart.feign.PmsFeign;
import cn.link.inkmall.cart.feign.SmsFeign;
import cn.link.inkmall.cart.feign.WmsFeign;
import cn.link.inkmall.cart.interceptor.LoginInterceptor;
import cn.link.inkmall.cart.service.CartService;
import cn.link.inkmall.pms.entity.SkuEntity;
import cn.link.inkmall.wms.entity.WareSkuEntity;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: Link
 * @Date: 2020/6/2 19:34
 * @Version 1.0
 */
public class CartServiceImpl implements CartService {

    private static final String CART_PREFIX = "cart:";

    private static final String CURRENT_PRICE_PREFIX = "cart:currentPrice:";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PmsFeign pmsFeign;

    @Autowired
    WmsFeign wmsFeign;

    @Autowired
    SmsFeign smsFeign;

    @Autowired
    ThreadPoolExecutor threadPool;

    @Override
    public void addCartItem(CartItem cartItem) {

        String key = generateKey();

        //通过 key 去redis中查询购物车
        BoundHashOperations<String, Object, Object> cartOps = stringRedisTemplate.boundHashOps(key);

        String skuId = cartItem.getSkuId().toString();
//        if (cartOps == null) {
//            //不存在key对应的购物车，创建一个购物车，并远程调用获取数据，存入商品
//            setCartItemByFeign(cartItem);
//            stringRedisTemplate.opsForHash().put(key, skuId, JSON.toJSONString(cartItem));
//            return;
//        }

        //注意了，redis 中的 hash 中的 key 都是以 string 存储的
        if (cartOps.hasKey(skuId)) {
            //如果购物车中存在该商品, 添加数量再存入即可
            String cartItemJsonStr = cartOps.get(skuId).toString();
            if (StringUtils.isNotBlank(cartItemJsonStr)) {
                Integer count = cartItem.getCount();
                cartItem = JSON.parseObject(cartItemJsonStr, CartItem.class);
                cartItem.setCount(cartItem.getCount() + count);
            }
        } else {
            //不存在该商品，远程调用获取信息存入redis
            setCartItemByFeign(cartItem);
        }

        cartOps.put(skuId, JSON.toJSONString(cartItem));

    }

    @Override
    public List<CartItem> queryCartItems() {

        //获取 用户登录信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        Long userId = userInfo.getUserId();

        List<CartItem> unloginCartItems = new ArrayList<>();

        //先获取 未登录 的购物车
        String unloginKey = CART_PREFIX + userKey;
        BoundHashOperations<String, Object, Object> unloginCartOps = stringRedisTemplate.boundHashOps(unloginKey);
        List<Object> unloginCartItemsObjList = unloginCartOps.values();
        if (!CollectionUtils.isEmpty(unloginCartItemsObjList)) {
            unloginCartItems = unloginCartItemsObjList.stream().map(unloginCartItemsObj -> {
                return JSON.parseObject(unloginCartItemsObj.toString(), CartItem.class);
            }).collect(Collectors.toList());
        }

        //若 userId 不存在则未登录，直接返回 未登录购物车 即可
        if (userId == null) {
            if (!CollectionUtils.isEmpty(unloginCartItems)) {
                //从redis中获取并设置购物车实时价格
                unloginCartItems.forEach(this::getCurrentPriceBySkuId);
            }
            return unloginCartItems;
        }

        String loginKey = CART_PREFIX + userId;
        BoundHashOperations<String, Object, Object> loginCartOps = stringRedisTemplate.boundHashOps(loginKey);
        //遍历 未登录 购物车，合并商品
        if (!CollectionUtils.isEmpty(unloginCartItems)) {

            unloginCartItems.forEach(cartItem -> {
                String skuId = cartItem.getSkuId().toString();
                if (loginCartOps.hasKey(skuId)) {
                    //有重复的就添加数量，没有就跳过直接存入
                    Integer count = cartItem.getCount();
                    String loginCartItemJsonStr = loginCartOps.get(skuId).toString();
                    cartItem = JSON.parseObject(loginCartItemJsonStr, CartItem.class);
                    cartItem.setCount(cartItem.getCount() + count);
                }

                //存入redis中
                loginCartOps.put(skuId, JSON.toJSONString(cartItem));
            });

        }

        //删除未登录购物车
        stringRedisTemplate.delete(unloginKey);

        List<Object> loginCartItemsObjList = loginCartOps.values();
        if (CollectionUtils.isEmpty(loginCartItemsObjList)) {
            // 未登录 已登录 购物车都没商品
            return null;
        }

        //返回已合并的已登录购物车
        List<CartItem> loginCartItems = loginCartItemsObjList
                .stream()
                .map(loginCartItemsObj -> {
                    CartItem cartItem = JSON.parseObject(loginCartItemsObj.toString(), CartItem.class);
                    //从redis中获取并设置购物车实时价格
                    getCurrentPriceBySkuId(cartItem);

                    return cartItem;
                })
                .collect(Collectors.toList());


        return loginCartItems;
    }

    private void getCurrentPriceBySkuId(CartItem cartItem) {
        //从redis中获取并设置购物车实时价格
        String currentPriceStr = stringRedisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX + cartItem.getSkuId());
        if (StringUtils.isNotBlank(currentPriceStr)) {
            cartItem.setCurrentPrice(new BigDecimal(currentPriceStr));
        }
    }

    @Override
    public void updateCount(CartItem cartItem) {

        String key = generateKey();
        String skuId = cartItem.getSkuId().toString();

        if (StringUtils.isBlank(skuId)) {
            return;
        }

        BoundHashOperations<String, Object, Object> cartOps = stringRedisTemplate.boundHashOps(key);
        if (!cartOps.hasKey(skuId)) {
            return;
        }

        String cartItemJsonStr = cartOps.get(skuId).toString();
        if (StringUtils.isNotBlank(cartItemJsonStr)) {
            Integer count = cartItem.getCount();
            cartItem = JSON.parseObject(cartItemJsonStr, CartItem.class);
            cartItem.setCount(cartItem.getCount() + count);
            cartOps.put(skuId, JSON.toJSONString(cartItem));
        }

    }

    @Override
    public void deleteCartItem(Long skuId) {

        String key = generateKey();

        BoundHashOperations<String, Object, Object> cartOps = stringRedisTemplate.boundHashOps(key);
        if (cartOps.hasKey(skuId.toString())) {

            cartOps.delete(skuId);

        }


    }

    @Override
    public void updateCheck(CartItem cartItem) {

        String key = generateKey();

        String skuId = cartItem.getSkuId().toString();

        BoundHashOperations<String, Object, Object> cartOps = stringRedisTemplate.boundHashOps(key);
        if (cartOps.hasKey(skuId)) {
            String cartItemJsonStr = cartOps.get(skuId).toString();
            if (StringUtils.isNotBlank(cartItemJsonStr)) {
                Boolean check = cartItem.getCheck();
                cartItem = JSON.parseObject(cartItemJsonStr, CartItem.class);
                cartItem.setCheck(check);
                cartOps.put(skuId, JSON.toJSONString(cartItem));
            }

        }

    }

    @Override
    public List<CartItem> getCheckedCartItemsByUserId(Long userId) {

        BoundHashOperations<String, Object, Object> loginCartOps = stringRedisTemplate.boundHashOps(CART_PREFIX + userId);
        List<Object> cartItemsObjs = loginCartOps.values();
        if (CollectionUtils.isEmpty(cartItemsObjs)) {
            return null;
        }

        return cartItemsObjs
                .stream()
                .map(cartItemsObj -> JSON.parseObject(cartItemsObj.toString(), CartItem.class))
                .filter(CartItem::getCheck)
                .collect(Collectors.toList());

    }

    private void setCartItemByFeign(CartItem cartItem) {
        //使用异步编排优化远程调用
        CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
            SkuEntity skuEntity = pmsFeign.querySkuById(cartItem.getSkuId()).getData();
            //将实时价格缓存到redis中以便购物车查询
            if (skuEntity != null) {
                stringRedisTemplate.opsForValue().set(CURRENT_PRICE_PREFIX + skuEntity.getId(), skuEntity.getPrice().toString());
                BeanUtils.copyProperties(skuEntity, cartItem);
            }
        });

        CompletableFuture<Void> skuAttrValueFuture = CompletableFuture.runAsync(() -> {
            cartItem.setSaleAttrs(pmsFeign.getSkuAttrValuesBySkuId(cartItem.getSkuId()).getData());
        });

        CompletableFuture<Void> saleFuture = CompletableFuture.runAsync(() -> {
            cartItem.setSales(smsFeign.getItemSaleVo(cartItem.getSkuId()).getData());
        });

        CompletableFuture<Void> wareFuture = CompletableFuture.runAsync(() -> {
            List<WareSkuEntity> wareSkuEntities = wmsFeign.getWareSkuEntitiesBySkuId(cartItem.getSkuId()).getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                cartItem.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
            }
        });

        CompletableFuture.allOf(skuInfoFuture, skuAttrValueFuture, saleFuture, wareFuture).join();
    }

    /**
     * 生成该 user 存放于 redis 中的 cart 的 key
     * @return
     */
    private String generateKey() {
        String key = CART_PREFIX;

        //获取 用户登录信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        Long userId = userInfo.getUserId();

        if (userId != null) {
            //userId存在就以userId作为key去 redis 中查询
            key += userId;

        } else {
            //userId不存在就以userKey作为key去 redis 中查询
            key += userKey;
        }
        return key;
    }
}

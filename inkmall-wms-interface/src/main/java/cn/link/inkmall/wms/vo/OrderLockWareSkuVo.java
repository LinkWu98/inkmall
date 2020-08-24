package cn.link.inkmall.wms.vo;

import lombok.Data;

/**
 * 库存锁定对象模型
 * @Author: Link
 * @Date: 2020/6/5 16:07
 * @Version 1.0
 */
@Data
public class OrderLockWareSkuVo {

    private Long skuId;

    private Integer count;

    /**
     * 锁定状态
     */
    private Boolean locked;

    /**
     * 锁定库存的仓库id，方便回滚时操作
     */
    private Long wareSkuId;

    /**
     * 订单号, 供秒杀成功后锁定库存和生成订单使用
     */
    private String orderToken;


    /**
     * userId，供秒杀成功后生成订单使用
     */
    private Long userId;

}

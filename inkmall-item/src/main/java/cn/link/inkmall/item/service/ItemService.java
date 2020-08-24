package cn.link.inkmall.item.service;

import cn.link.inkmall.item.vo.ItemVo;

/**
 * @Author: Link
 * @Date: 2020/5/31 14:22
 * @Version 1.0
 */
public interface ItemService {
    ItemVo getItemsDetail(Long skuId);
}

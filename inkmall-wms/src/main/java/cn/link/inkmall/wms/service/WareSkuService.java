package cn.link.inkmall.wms.service;

import cn.link.inkmall.wms.vo.OrderLockWareSkuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.wms.entity.WareSkuEntity;

import java.util.List;

/**
 * 商品库存
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:52:38
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 查询并锁定商品库存
     */
    void checkAndLockStock(List<OrderLockWareSkuVo> orderLockWareSkuVos, String orderToken);
}


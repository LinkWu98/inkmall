package cn.link.inkmall.sms.service;

import cn.link.inkmall.sms.vo.ItemSaleVo;
import cn.link.inkmall.sms.vo.SaleVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.sms.entity.SkuBoundsEntity;

import java.util.List;

/**
 * 商品spu积分设置
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:08:16
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveSaleInfo(SaleVo saleVo);

    List<ItemSaleVo> getItemSaleVo(Long skuId);
}


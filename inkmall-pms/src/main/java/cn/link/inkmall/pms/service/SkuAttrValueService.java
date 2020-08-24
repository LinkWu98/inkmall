package cn.link.inkmall.pms.service;

import cn.link.inkmall.pms.vo.SkuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.pms.entity.SkuAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
public interface SkuAttrValueService extends IService<SkuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveSkuAttrs(SkuVo skuVo);

    List<SkuAttrValueEntity> getSkuSearchAttrValueBySkuIdAndAttrIds(Long skuId, List<Long> attrIds);
}


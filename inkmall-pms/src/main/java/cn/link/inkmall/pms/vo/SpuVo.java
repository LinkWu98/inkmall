package cn.link.inkmall.pms.vo;

import cn.link.inkmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

/**
 * 封装了页面的新增spu对象数据
 *
 * @Author: Link
 * @Date: 2020/5/14 15:46
 * @Version 1.0
 */
@Data
public class SpuVo extends SpuEntity {

    /**
     * spu对象的描述 pms_spu_desc
     */
    private List<String> spuImages;

    /**
     * spu对象的基本属性
     */
    private List<SpuAttrValueVo> baseAttrs;


    /**
     * sku对象及其销售属性，及其对应的营销数据sms_xxx
     */
    private List<SkuVo> skus;


}

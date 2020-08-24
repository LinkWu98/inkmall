package cn.link.inkmall.pms.service;

import cn.link.inkmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.pms.entity.SkuEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
public interface SkuService extends IService<SkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveSkuAndSaleInfo(SpuVo spuVo);
}


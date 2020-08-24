package cn.link.inkmall.pms.service;

import cn.link.inkmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.pms.entity.SpuAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
public interface SpuAttrValueService extends IService<SpuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveSpuAttrs(SpuVo spuVo);

    List<SpuAttrValueEntity> getSpuSearchAttrValueBySpuIdAndAttrIds(Long spuId, List<Long> attrIds);
}


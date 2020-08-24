package cn.link.inkmall.pms.service;

import cn.link.inkmall.pms.vo.ItemAttrGroupVo;
import cn.link.inkmall.pms.vo.SpuAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.pms.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<SpuAttrVo> getSpuAttrsByCid(Long cid);

    List<ItemAttrGroupVo> getItemAttrGroups(Long categoryId, Long spuId, Long skuId);
}


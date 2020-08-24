package cn.link.inkmall.pms.service;

import cn.link.inkmall.pms.vo.ItemAttrValueVo;
import cn.link.inkmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.pms.entity.SpuEntity;

import java.util.List;

/**
 * spu信息
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
public interface SpuService extends IService<SpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    PageResultVo getSpuListByCategoryId(Long categoryId, PageParamVo paramVo);

    void bigSave(SpuVo spuVo);

    List<ItemAttrValueVo> getItemAttrValuesBySpuId(Long spuId);
}


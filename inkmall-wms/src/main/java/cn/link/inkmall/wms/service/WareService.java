package cn.link.inkmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.wms.entity.WareEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:52:38
 */
public interface WareService extends IService<WareEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}


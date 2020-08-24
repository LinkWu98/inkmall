package cn.link.inkmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.wms.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:52:38
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}


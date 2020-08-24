package cn.link.inkmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.ums.entity.UserCollectShopEntity;

import java.util.Map;

/**
 * 
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:45:48
 */
public interface UserCollectShopService extends IService<UserCollectShopEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}


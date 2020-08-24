package cn.link.inkmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.sms.entity.HomeSubjectEntity;

import java.util.Map;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:08:16
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}


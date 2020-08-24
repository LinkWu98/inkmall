package cn.link.inkmall.ums.service;

import cn.link.inkmall.ums.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.ums.entity.UserEntity;

/**
 * 用户表
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:45:48
 */
public interface UserService extends IService<UserEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    Boolean check(String data, Integer type);

    void sendCode(String phone);

    void saveCode(String code, String phone);

    void doRegister(RegisterVo registerVo);

    UserEntity doLogin(String username, String password);
}


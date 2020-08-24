package cn.link.inkmall.ums.api;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.ums.entity.UserAddressEntity;
import cn.link.inkmall.ums.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/6/1 10:53
 * @Version 1.0
 */
public interface UmsApi {

    /**
     * 保存验证码
     */
    @PutMapping("ums/user/code/save/{code}/{phone}")
    ResponseVo saveCode(@PathVariable("code") String code,
                        @PathVariable("phone") String phone);

    @GetMapping("ums/user/query")
    ResponseVo<UserEntity> doLogin(@RequestParam("username") String username,
                                   @RequestParam("password") String password);

    /**
     * 通过 userId 获取地址信息
     */
    @GetMapping("addresses/{userId}")
    ResponseVo<List<UserAddressEntity>> getAddressesByUserId(@PathVariable("userId") Long userId);

    /**
     * 通过 userId 获取 用户信息
     */
    @GetMapping("ums/user/{id}")
    ResponseVo<UserEntity> queryUserById(@PathVariable("id") Long id);
}

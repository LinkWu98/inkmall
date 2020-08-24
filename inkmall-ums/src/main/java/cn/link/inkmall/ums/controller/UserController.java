package cn.link.inkmall.ums.controller;

import java.util.List;

import cn.link.inkmall.ums.vo.RegisterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.link.inkmall.ums.entity.UserEntity;
import cn.link.inkmall.ums.service.UserService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.bean.PageParamVo;

/**
 * 用户表
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:45:48
 */
@Api(tags = "用户表 管理")
@RestController
@RequestMapping("ums/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @GetMapping("query")
    public ResponseVo<UserEntity> doLogin(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {

        UserEntity userEntity = userService.doLogin(username, password);

        return ResponseVo.ok(userEntity);

    }

    /**
     * 用户注册
     */
    @PostMapping("register")
    public ResponseVo doRegister(@RequestBody RegisterVo registerVo) {

        userService.doRegister(registerVo);

        return ResponseVo.ok();

    }


    /**
     * 保存验证码
     */
    @PutMapping("code/save/{code}/{phone}")
    public ResponseVo saveCode(@PathVariable("code") String code,
                               @PathVariable("phone") String phone) {

        userService.saveCode(code, phone);

        return ResponseVo.ok();

    }

    /**
     * 用户请求发送短信验证码
     */
    @PostMapping("code")
    public ResponseVo sendCode(@RequestParam("phone") String phone) {

        userService.sendCode(phone);

        return ResponseVo.ok();

    }

    /**
     * 注册时的用户数据校验
     */
    @GetMapping("check/{data}/{type}")
    public ResponseVo<Boolean> check(@PathVariable("data") String data,
                                     @PathVariable("type") Integer type) {

        Boolean result = userService.check(data, type);

        return ResponseVo.ok(result);

    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo) {
        PageResultVo pageResultVo = userService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<UserEntity> queryUserById(@PathVariable("id") Long id) {
        UserEntity user = userService.getById(id);

        return ResponseVo.ok(user);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody UserEntity user) {
        userService.save(user);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody UserEntity user) {
        userService.updateById(user);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        userService.removeByIds(ids);

        return ResponseVo.ok();
    }

}

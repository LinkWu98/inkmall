package cn.link.inkmall.ums.service.impl;

import cn.link.inkmall.common.exception.InkmallException;
import cn.link.inkmall.ums.vo.RegisterVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.ums.mapper.UserMapper;
import cn.link.inkmall.ums.entity.UserEntity;
import cn.link.inkmall.ums.service.UserService;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private static final String CODE_PREFIX = "ums:register:code:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${code.expire}")
    private Long expire;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public Boolean check(String data, Integer type) {

        if (StringUtils.isEmpty(data) || type == null) {
            return false;
        }

        //判断 data 的类型去数据库进行校验
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1 : wrapper.eq("username", data); break;
            case 2 : wrapper.eq("phone", data); break;
            case 3 : wrapper.eq("email", data); break;
            default: return false;
        }

        Integer count = baseMapper.selectCount(wrapper);

        return count == 0;

    }

    @Override
    public void sendCode(String phone) {

        if (StringUtils.isEmpty(phone)) {
            throw new InkmallException("您输入的手机号不合法");
        }

        //发送验证码
        //1. 此处先判断缓存中是否存在，存在就告知客户端
        String code = stringRedisTemplate.opsForValue().get(CODE_PREFIX + phone);
        if (!StringUtils.isEmpty(code)) {
            throw new InkmallException("您还有未过期的验证码，请确认后输入 (验证码过期时间为 1 分钟)");
        }

        //2. 不存在就发送消息至消息队列，让消息队列工程调用第三方api接口发送验证码，发送验证码成功后存一份至缓存中
        rabbitTemplate.convertAndSend("ums-code-exchange", "code.send", phone);

    }

    @Override
    public void saveCode(String code, String phone) {

        if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(phone)) {
            //保存验证码
            stringRedisTemplate.opsForValue().set(CODE_PREFIX + phone, code, expire, TimeUnit.MINUTES);
        }

    }

    @Override
    public void doRegister(RegisterVo registerVo) {

        if (registerVo == null) {
            return;
        }

        String code = registerVo.getCode();
        if (!StringUtils.isNotBlank(code)) {
            throw new InkmallException("请输入验证码");
        }

        String password = registerVo.getPassword();
        if (!StringUtils.isNotBlank(password)) {
            throw new InkmallException("请输入密码");
        }

        String username = registerVo.getUsername();
        if (!StringUtils.isNotBlank(username)) {
            throw new InkmallException("请输入用户名");
        }

        String email = registerVo.getEmail();
        if (!StringUtils.isNotBlank(email)) {
            throw new InkmallException("请输入邮箱");
        }

        String mobile = registerVo.getMobile();
        if (!StringUtils.isNotBlank(email)) {
            throw new InkmallException("请输入手机号");
        }

        String savedCode = stringRedisTemplate.opsForValue().get(CODE_PREFIX + mobile);

        //校验验证码
        if (!StringUtils.isNotBlank(savedCode)) {
            //手机号对应的验证码不存在，重新发送
            throw new InkmallException("您的验证码已过期或不存在，请重新发送");
        }

        if (!StringUtils.equals(code, savedCode)) {
            //验证码不一致
            throw new InkmallException("您输入的验证码错误，请重新输入");
        }

        //验证码正确，删除验证码，防止表单重复提交
        stringRedisTemplate.delete(CODE_PREFIX + mobile);

        //查询用户是否存在
        Integer count = baseMapper.selectCount(new QueryWrapper<UserEntity>().eq("username", username));

        if (count != 0) {
            throw new InkmallException("该用户已被注册");
        }

        //给密码加盐加密
        String salt = UUID.randomUUID().toString().substring(0, 6);
        String codedPassword = DigestUtils.md5Hex(password + salt);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(codedPassword);
        userEntity.setPhone(mobile);
        userEntity.setEmail(email);
        userEntity.setLevelId(1L);
        userEntity.setSourceType(1);
        userEntity.setSalt(salt);
        userEntity.setGrowth(1000);
        userEntity.setIntegration(1000);
        userEntity.setStatus(1);
        userEntity.setCreateTime(new Date());


        baseMapper.insert(userEntity);
    }

    @Override
    public UserEntity doLogin(String username, String password) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new InkmallException("账号或密码不能为空");
        }


        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));

        if (userEntity == null) {
            throw new InkmallException("该账号不存在");
        }

        //加盐加密后在进行比较
        String salt = userEntity.getSalt();

        String codedPassword = DigestUtils.md5Hex(password + salt);

        if (!StringUtils.equals(userEntity.getPassword(), codedPassword)) {
            throw new InkmallException("密码错误");
        }

        return userEntity;

    }

}
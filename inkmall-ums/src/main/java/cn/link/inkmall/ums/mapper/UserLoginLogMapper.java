package cn.link.inkmall.ums.mapper;

import cn.link.inkmall.ums.entity.UserLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登陆记录表
 * 
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:45:48
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLogEntity> {
	
}

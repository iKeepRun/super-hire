package com.zack.service.impl;
import com.zack.domain.Users;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.mapper.UsersMapper;
import com.zack.service.UsersService;
import org.springframework.stereotype.Service;

/**
* @author mczq
* @description 针对表【users(用户表)】的数据库操作Service实现
* @createDate 2025-04-21 13:42:40
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService {

}





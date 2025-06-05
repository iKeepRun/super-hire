package com.zack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.domain.Users;
import com.zack.dto.ModifyUserDTO;

/**
* @author mczq
* @description 针对表【users(用户表)】的数据库操作Service
* @createDate 2025-04-21 13:42:40
*/
public interface UsersService {
    void modifyUserInfo(ModifyUserDTO  modifyUserDTO);
    public Users getById(String uid);
    Long getCountsByCompanyId(String companyId);
}

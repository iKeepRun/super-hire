package com.zack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.common.CommonPage;
import com.zack.domain.Users;
import com.zack.dto.ModifyUserDTO;

import java.util.List;

/**
 * @author mczq
 * @description 针对表【users(用户表)】的数据库操作Service
 * @createDate 2025-04-21 13:42:40
 */
public interface UsersService {
    void modifyUserInfo(ModifyUserDTO modifyUserDTO);

    public Users getById(String uid);

    Long getCountsByCompanyId(String companyId);

    void updateUserCompanyId(String hrUserId,
                             String realname,
                             String companyId);

    void updateUserToHR(String uid);

    void updateUserToCand(String hrUserId);

    CommonPage getHRList(String companyId, Integer page, Integer limit);

    public List<Users> getByIds(List<String> userIds);
}

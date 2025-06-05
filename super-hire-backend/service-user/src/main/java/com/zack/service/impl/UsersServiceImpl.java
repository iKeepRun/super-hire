package com.zack.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zack.domain.Users;
import com.zack.dto.ModifyUserDTO;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.mapper.UsersMapper;
import com.zack.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;
    @Override
    public void modifyUserInfo(ModifyUserDTO  userDTO) {
        String userId = userDTO.getUserId();
        ThrowUtil.throwIf(StrUtil.isBlank(userId), ErrorCode.PARAMS_ERROR);

        Users users = BeanUtil.copyProperties(userDTO, Users.class);

        users.setCreatedTime(LocalDateTime.now());
        users.setUpdatedTime(LocalDateTime.now());
        usersMapper.updateById(users);

    }

    @Override
    public Users getById(String uid) {
        return usersMapper.selectById(uid);
    }

    @Override
    public Long getCountsByCompanyId(String companyId) {

        Long counts = usersMapper.selectCount(
                new QueryWrapper<Users>()
                        .eq("hr_in_which_company_id", companyId)
        );

        return counts;
    }
}

package com.zack.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonPage;
import com.zack.domain.Users;
import com.zack.dto.ModifyUserDTO;
import com.zack.enums.UserRole;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.mapper.UsersMapper;
import com.zack.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {
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


    @Transactional
    @Override
    public void updateUserCompanyId(String hrUserId,
                                    String realname,
                                    String companyId) {
        Users hrUser = new Users();
        hrUser.setId(hrUserId);
        hrUser.setRealName(realname);
        hrUser.setHrInWhichCompanyId(companyId);

        hrUser.setUpdatedTime(LocalDateTime.now());

        usersMapper.updateById(hrUser);
    }

    @Transactional
    @Override
    public void updateUserToHR(String uid) {

        Users hrUser = new Users();
        hrUser.setId(uid);
        hrUser.setRole(UserRole.RECRUITER.type);

        hrUser.setUpdatedTime(LocalDateTime.now());

        usersMapper.updateById(hrUser);
    }

    @Override
    public CommonPage getHRList(String companyId, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Users> users = usersMapper.selectList(new QueryWrapper<Users>().eq("hr_in_which_company_id", companyId));
        return setPage(users,page);
    }
}

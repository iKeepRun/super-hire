package com.zack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonPage;
import com.zack.domain.Admin;
import com.zack.dto.AdminDTO;
import com.zack.dto.CreateAdminDTO;
import com.zack.dto.UpdateAdminDTO;
import com.zack.exceptions.BusinessException;
import com.zack.exceptions.ErrorCode;
import com.zack.mapper.AdminMapper;
import com.zack.service.AdminService;
import com.zack.utils.MD5Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Service
public class AdminServiceImpl extends BaseInfoProperties implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Transactional
    @Override
    public void createAdmin(CreateAdminDTO createAdminDTO) {

        // admin账号判断是否存在，如果存在，则禁止账号分配
        Admin admin = getSelfAdmin(createAdminDTO.getUsername());
        // 优雅异常解耦完美体现
        if (admin != null) {
            throw  new BusinessException(ErrorCode.ADMIN_USERNAME_EXIST_ERROR);
        }

        // 创建账号
        Admin newAdmin = new Admin();
        BeanUtils.copyProperties(createAdminDTO, newAdmin);

        // 生成随机数字或者英文字母结合的盐
        String slat = (int)((Math.random() * 9 + 1) * 100000) + "";
        String pwd = MD5Utils.encrypt(createAdminDTO.getPassword(), slat);
        newAdmin.setPassword(pwd);
        newAdmin.setSlat(slat);

        newAdmin.setCreate_time(new Date());
        newAdmin.setUpdated_time(new Date());

        adminMapper.insert(newAdmin);
    }

    @Override
    public CommonPage<Admin> getAdminList(String accountName,
                                   Integer page,
                                   Integer limit) {

        PageHelper.startPage(page, limit);

        List<Admin> adminList = adminMapper.selectList(
                new QueryWrapper<Admin>()
                        .like("username", accountName)
        );

        return setPage(adminList, page);
    }

    @Override
    public void deleteAdmin(String username) {

        int res = adminMapper.delete(
                new QueryWrapper<Admin>()
                        .eq("username", username)
                        .ne("username", "admin")
        );

        if (res == 0) {
            throw  new BusinessException(ErrorCode.ADMIN_DELETE_ERROR);
        }
    }

    private Admin getSelfAdmin(String username) {
        Admin admin = adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username", username)
        );
        return admin;
    }

    @Override
    public Admin getById(String adminId) {
        return adminMapper.selectById(adminId);
    }

    @Transactional
    @Override
    public void updateAdmin(UpdateAdminDTO adminDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDTO, admin);
        admin.setUpdated_time(new Date());
        adminMapper.updateById(admin);
    }


}


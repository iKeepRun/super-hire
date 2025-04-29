package com.zack.service.impl;

import com.zack.dto.AdminDTO;
import com.zack.service.AdminService;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.mapper.AdminMapper;
import com.zack.domain.Admin;
import com.zack.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean adminLogin(AdminDTO adminDTO) {

        // 根据用户名获得盐slat
//        Admin admin = adminMapper.selectOne(
//                new QueryWrapper<Admin>()
//                        .eq("username", adminDTO.getUsername())
//        );
        Admin admin = getSelfAdmin(adminDTO.getUsername());

        // 判断admin是否为空来返回true或false
        if (admin == null) {
            return false;
        } else {
            String slat = admin.getSlat();
            String md5Str = MD5Utils.encrypt(adminDTO.getPassword(), slat);
            if (md5Str.equalsIgnoreCase(admin.getPassword())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Admin getAdminInfo(AdminDTO adminDTO) {
        return this.getSelfAdmin(adminDTO.getUsername());
    }

    private Admin getSelfAdmin(String username) {
        Admin admin = adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username", username)
        );
        return admin;
    }
}


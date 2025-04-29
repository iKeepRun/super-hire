package com.zack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.domain.Admin;
import com.zack.dto.AdminDTO;

public interface AdminService extends IService<Admin> {

    /**
     * admin 登录
     * @param adminDTO
     * @return
     */
    public boolean adminLogin(AdminDTO adminDTO);

    /**
     * 获得admin信息
     * @param adminDTO
     * @return
     */
    public Admin getAdminInfo(AdminDTO adminDTO);
}

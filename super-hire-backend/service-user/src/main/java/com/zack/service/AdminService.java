package com.zack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.common.CommonPage;
import com.zack.domain.Admin;
import com.zack.dto.AdminDTO;
import com.zack.dto.CreateAdminDTO;
import com.zack.dto.UpdateAdminDTO;

public interface AdminService {

    /**
     * 创建admin账号
     * @param createAdminDTO
     */
    public void createAdmin(CreateAdminDTO createAdminDTO);

    /**
     * 查询admin列表
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    public CommonPage getAdminList(String accountName,
                                   Integer page,
                                   Integer limit);

    /**
     * 删除admin账号
     * @param username
     */
    public void deleteAdmin(String username);

    /**
     * 查询admin信息
     * @param adminId
     * @return
     */
    public Admin getById(String adminId);

    /**
     * 更新admin信息
     * @param updateAdminDTO
     */
    public void updateAdmin(UpdateAdminDTO updateAdminDTO);
}

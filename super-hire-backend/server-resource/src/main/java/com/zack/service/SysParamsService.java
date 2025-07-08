package com.zack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.domain.SysParams;

/**
* @author chenzhiqiang
* @description 针对表【sys_params(系统参数配置表，本表仅有一条记录)】的数据库操作Service
* @createDate 2025-05-17 10:19:37
*/
public interface SysParamsService {
    /**
     * 更新简历的最大刷新次数
     * @param maxCounts
     * @param version
     */
    void updateMaxResumeRefreshCountsSysParams(Integer maxCounts,Integer version);

}

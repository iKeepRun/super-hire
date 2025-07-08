package com.zack.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.base.BaseInfoProperties;
import com.zack.domain.SysParams;
import com.zack.mapper.SysParamsMapper;
import com.zack.service.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author chenzhiqiang
* @description 针对表【sys_params(系统参数配置表，本表仅有一条记录)】的数据库操作Service实现
* @createDate 2025-05-17 10:19:37
*/
@Service
public class SysParamsServiceImpl extends BaseInfoProperties
    implements SysParamsService{

    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Override
    public void updateMaxResumeRefreshCountsSysParams(Integer maxCounts, Integer version) {
        SysParams sysParams=new SysParams();
        sysParams.setId(SYS_PARAMS_PK);
        sysParams.setMaxResumeRefreshCounts(maxCounts);

        sysParamsMapper.updateById(sysParams);
    }
}





package com.zack.service;

import com.zack.domain.Industry;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
* @author chenzhiqiang
* @description 针对表【industry(行业表)】的数据库操作Service
* @createDate 2025-05-17 10:19:37
*/
@Mapper
public interface IndustryService extends IService<Industry> {
    /**
     * 根据名称判断是否存在
     * @param nodeName
     * @return
     */
     boolean getIndustryIsExistByName(String nodeName);
     void createIndustry(Industry industry);
}

package com.zack.service;

import com.zack.bo.DataDictionaryBO;
import com.zack.domain.DataDictionary;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author chenzhiqiang
* @description 针对表【data_dictionary(数据字典表)】的数据库操作Service
* @createDate 2025-05-17 10:19:37
*/
public interface DataDictionaryService extends IService<DataDictionary> {
    /**
     * 创建或者更新数据字典
     * @param dataDictionaryBO
     */
    public void createOrUpdateDataDictionary(DataDictionaryBO dataDictionaryBO);
}

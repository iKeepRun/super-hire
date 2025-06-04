package com.zack.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.DataDictionaryBO;
import com.zack.common.CommonPage;
import com.zack.common.ResponseStatusEnum;
import com.zack.domain.DataDictionary;
import com.zack.enums.YesOrNo;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.service.DataDictionaryService;
import com.zack.mapper.DataDictionaryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author chenzhiqiang
* @description 针对表【data_dictionary(数据字典表)】的数据库操作Service实现
* @createDate 2025-05-17 10:19:37
*/
@Service
public class DataDictionaryServiceImpl extends BaseInfoProperties
    implements DataDictionaryService{

    @Autowired
    private DataDictionaryMapper dataDictionaryMapper;

    @Transactional
    @Override
    public void createOrUpdateDataDictionary(DataDictionaryBO dataDictionaryBO) {

        DataDictionary dataDictionary = new DataDictionary();
        BeanUtils.copyProperties(dataDictionaryBO, dataDictionary);

        if (StrUtil.isBlank(dataDictionaryBO.getId())) {
            // 如果id为空，则新增新的数据字典项

            // 判断数据字典项是不可重复的
            DataDictionary ddExist =  dataDictionaryMapper.selectOne(
                    new QueryWrapper<DataDictionary>()
                            .eq("item_key", dataDictionaryBO.getItemKey())
                            .eq("item_value", dataDictionaryBO.getItemValue())
            );
            // if (ddExist != null) {
            //     GraceException.display(ResponseStatusEnum.DATA_DICT_EXIST_ERROR);
            // }
            ThrowUtil.throwIf(ddExist != null, ErrorCode.DATA_DICT_EXIST_ERROR);

            dataDictionaryMapper.insert(dataDictionary);
        } else {
            // 否则id不为空，则修改数据字典项
            dataDictionaryMapper.updateById(dataDictionary);
        }

    }


    @Override
    public CommonPage<DataDictionary> getDataDictListPaged(String typeName, String itemValue, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<DataDictionary> dataDictionaryList = dataDictionaryMapper.selectList(
                new QueryWrapper<DataDictionary>()
                        .like("type_name", typeName)
                        .like("item_value", itemValue)
                        .orderByAsc("type_code")
                        .orderByAsc("sort"));


        return setPage(dataDictionaryList,page);
    }


    @Override
    public DataDictionary getDataDictionary(String dictId) {
        return dataDictionaryMapper.selectById(dictId);
    }

    @Transactional
    @Override
    public void deleteDataDictionary(String dictId) {
        int res = dataDictionaryMapper.deleteById(dictId);
        ThrowUtil.throwIf(res == 0,ErrorCode.DATA_DICT_DELETE_ERROR);
        // if (res == 0 ) GraceException.display(ResponseStatusEnum.DATA_DICT_DELETE_ERROR);
    }
    @Override
    public List<DataDictionary> getDataByCode(String typeCode) {

        List<DataDictionary> ddList = dataDictionaryMapper.selectList(
                new QueryWrapper<DataDictionary>()
                        .eq("type_code", typeCode)
                        .eq("enable", YesOrNo.YES.type)
                        .orderByAsc("sort")
        );

        return ddList;
    }
}





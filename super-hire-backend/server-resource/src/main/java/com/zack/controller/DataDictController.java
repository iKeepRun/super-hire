package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.DataDictionaryBO;
import com.zack.common.CommonPage;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.domain.DataDictionary;
import com.zack.exceptions.ErrorCode;
import com.zack.service.DataDictionaryService;
import com.zack.service.IndustryService;
import com.zack.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("dataDict")
public class DataDictController extends BaseInfoProperties{
    private static final String DDKEY_PREFIX = DATA_DICTIONARY_LIST_TYPECODE + ":";
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private IndustryService industryService;

    /**
     * 创建数据字典
     *
     * @param dataDictionaryBO
     * @return
     */
    @PostMapping("create")
    public CommonResult create(
            @RequestBody @Valid DataDictionaryBO dataDictionaryBO) {

        dataDictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return CommonResult.success();
    }

    @PostMapping("list")
    public CommonResult<CommonPage<DataDictionary>> list(String typeName,
                                                         String itemValue,
                                                         @RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "20") Integer limit) {
       CommonPage commonPage=dataDictionaryService.getDataDictListPaged( typeName, itemValue, page, limit);

       return CommonResult.success(commonPage);
    }

    /**
     * 修改数据字典
     * @param dataDictionaryBO
     * @return
     */
    @PostMapping("modify")
    public CommonResult modify(
            @RequestBody @Valid DataDictionaryBO dataDictionaryBO) {

        if (StrUtil.isBlank(dataDictionaryBO.getId())) {
            return CommonResult.error(ErrorCode.PARAMS_ERROR);
        }

        dataDictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return CommonResult.success();
    }

    /**
     * 根据id查询数据字典某一项
     * @param dictId
     * @return
     */
    @PostMapping("item")
    public CommonResult item(String dictId) {
        DataDictionary dd = dataDictionaryService.getDataDictionary(dictId);
        return CommonResult.success(dd);
    }

    /**
     * 删除数据字典
     * @param dictId
     * @return
     */
    @PostMapping("delete")
    public CommonResult delete(String dictId) {
        dataDictionaryService.deleteDataDictionary(dictId);
        return CommonResult.success();
    }


    /**
     * 根据字典码获得该分类下的所有数据字典项的列表
     * @param typeCode
     * @return
     */
    @PostMapping("app/getDataByCode")
    public CommonResult getDataByCode(String typeCode) {

        if (StrUtil.isBlank(typeCode)) {
            return CommonResult.error(ErrorCode.PARAMS_ERROR);
        }

        String ddkey = DDKEY_PREFIX + typeCode;

        String ddListStr = redis.get(ddkey);
        List<DataDictionary> list = null;
        if (StrUtil.isNotBlank(ddListStr)) {
            list = GsonUtils.stringToListAnother(ddListStr, DataDictionary.class);
        }

        // 只从redis中查询，如果没有就没有，也不需要从数据库中查询，完全避免缓存的穿透击穿雪崩问题
//        List<DataDictionary> list = dictionaryService.getDataByCode(typeCode);
        return CommonResult.success(list);
    }
}

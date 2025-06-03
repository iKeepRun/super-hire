package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.bo.DataDictionaryBO;
import com.zack.common.CommonPage;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.domain.DataDictionary;
import com.zack.service.DataDictionaryService;
import com.zack.service.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("dataDict")
public class DataDictController {

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
    public GraceJSONResult create(
            @RequestBody @Valid DataDictionaryBO dataDictionaryBO) {

        dataDictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return GraceJSONResult.ok();
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
    public GraceJSONResult modify(
            @RequestBody @Valid DataDictionaryBO dataDictionaryBO) {

        if (StrUtil.isBlank(dataDictionaryBO.getId())) {
            return GraceJSONResult.error();
        }

        dataDictionaryService.createOrUpdateDataDictionary(dataDictionaryBO);
        return GraceJSONResult.ok();
    }

    /**
     * 根据id查询数据字典某一项
     * @param dictId
     * @return
     */
    @PostMapping("item")
    public GraceJSONResult item(String dictId) {
        DataDictionary dd = dataDictionaryService.getDataDictionary(dictId);
        return GraceJSONResult.ok(dd);
    }

    /**
     * 删除数据字典
     * @param dictId
     * @return
     */
    @PostMapping("delete")
    public GraceJSONResult delete(String dictId) {
        dataDictionaryService.deleteDataDictionary(dictId);
        return GraceJSONResult.ok();
    }
}

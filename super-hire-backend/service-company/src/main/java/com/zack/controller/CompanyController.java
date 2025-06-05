package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.CreateCompanyBO;
import com.zack.bo.QueryCompanyBO;
import com.zack.bo.ReviewCompanyBO;
import com.zack.common.CommonPage;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.domain.Company;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.feign.UserInfoMicroFeign;
import com.zack.service.CompanyService;
import com.zack.utils.JsonUtils;
import com.zack.vo.CompanySimpleVO;
import com.zack.vo.UsersVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/company")
public class CompanyController extends BaseInfoProperties {
  @Autowired
  private CompanyService companyService;

  @Autowired
  private UserInfoMicroFeign userInfoMicroFeign;
    /**
     * 根据全称查询企业信息
     * @param fullName
     * @return
     */
    @PostMapping("getByFullName")
    public CommonResult getByFullName(String fullName) {

        ThrowUtil.throwIf(StrUtil.isBlank(fullName), ErrorCode.PARAMS_ERROR);

        Company company = companyService.getByFullName(fullName);
        if (company == null) return CommonResult.success(null);

        CompanySimpleVO companySimpleVO = new CompanySimpleVO();
        BeanUtils.copyProperties(company, companySimpleVO);

        return CommonResult.success(companySimpleVO);
    }

    /**
     * 创建待审核的企业或者重新发起审核
     * @param createCompanyBO
     * @return
     */
    @PostMapping("createNewCompany")
    public CommonResult createNewCompany(
            @RequestBody @Valid CreateCompanyBO createCompanyBO) {
        // TODO 课后自行校验 CreateCompanyBO
        String companyId = createCompanyBO.getCompanyId();
        String doCompanyId = "";
        if (StrUtil.isBlank(companyId)) {
            // 如果为空，则创建公司
            doCompanyId = companyService.createNewCompany(createCompanyBO);
        } else {
            // 否则不为空，则在原有的公司信息基础上做修改
            doCompanyId = companyService.resetNewCompany(createCompanyBO);
        }

        return CommonResult.success(doCompanyId);
    }


    /**
     * 获得企业信息
     * @param companyId
     * @param withHRCounts
     * @return
     */
    @PostMapping("getInfo")
    public CommonResult getInfo(String companyId, boolean withHRCounts) {

        CompanySimpleVO companySimpleVO = getCompany(companyId);
        // 根据companyId获得旗下有多少个hr绑定，微服务的远程调用
        if (withHRCounts && companySimpleVO != null) {
            CommonResult graceJSONResult =
                    userInfoMicroFeign.getCountsByCompanyId(companyId);
            Object data = graceJSONResult.getData();
            Long hrCounts = Long.valueOf(data.toString());
            companySimpleVO.setHrCounts(hrCounts);
        }

        return CommonResult.success(companySimpleVO);
    }

    private CompanySimpleVO getCompany(String companyId) {
        if (StrUtil.isBlank(companyId)) return null;

        String companyJson = redis.get(REDIS_COMPANY_BASE_INFO + ":" + companyId);
        if (StrUtil.isBlank(companyJson)) {
            // 查询数据库
            Company company = companyService.getById(companyId);
            if (company == null) {
                return null;
            }

            CompanySimpleVO simpleVO = new CompanySimpleVO();
            BeanUtils.copyProperties(company, simpleVO);

            redis.set(REDIS_COMPANY_BASE_INFO + ":" + companyId,
                    new Gson().toJson(simpleVO),
                    1 * 60);
            return simpleVO;
        } else {
            // 不为空，直接转换对象
            return new Gson().fromJson(companyJson, CompanySimpleVO.class);
        }
    }



    /**
     * 提交企业的审核信息
     * @param reviewCompanyBO
     * @return
     */
    @PostMapping("goReviewCompany")
    @GlobalTransactional
    public CommonResult goReviewCompany(
            @RequestBody @Valid ReviewCompanyBO reviewCompanyBO) {

        // 1. 微服务调用，绑定HR企业id
        CommonResult result = userInfoMicroFeign.bindingHRToCompany(
                reviewCompanyBO.getHrUserId(),
                reviewCompanyBO.getRealname(),
                reviewCompanyBO.getCompanyId());
        String hrMobile = result.getData().toString();
//        System.out.println(hrMobile);

        // 2. 保存审核信息，修改状态为[3：审核中（等待审核）]
        reviewCompanyBO.setHrMobile(hrMobile);
        companyService.commitReviewCompanyInfo(reviewCompanyBO);

        return CommonResult.success();
    }


    /**
     * 根据hr的用户id查询最新的企业信息
     * @param hrUserId
     * @return
     */
    @PostMapping("information")
    public GraceJSONResult information(String hrUserId) {

        UsersVO hrUser = getHRInfoVO(hrUserId);

        CompanySimpleVO company = getCompany(hrUser.getHrInWhichCompanyId());

        return GraceJSONResult.ok(company);
    }

    private UsersVO getHRInfoVO(String hrUserId) {
        CommonResult commonResult = userInfoMicroFeign.get(hrUserId);
        Object data = commonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO hrUser = JsonUtils.jsonToPojo(json, UsersVO.class);
        return hrUser;
    }


    // **************************** 以上为用户端所使用 ****************************

    // **************************** 以下为运营平台所使用 ****************************

    @PostMapping("admin/getCompanyList")
    public CommonResult adminGetCompanyList(
            @RequestBody @Valid QueryCompanyBO companyBO,
            Integer page,
            Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        CommonPage commonPage = companyService.queryCompanyList(
                companyBO,
                page,
                limit);
        return CommonResult.success(commonPage);
    }
}

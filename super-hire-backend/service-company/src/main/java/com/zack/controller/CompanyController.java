package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.CreateCompanyBO;
import com.zack.bo.ModifyCompanyInfoBO;
import com.zack.bo.QueryCompanyBO;
import com.zack.bo.ReviewCompanyBO;
import com.zack.common.CommonPage;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.common.ResponseStatusEnum;
import com.zack.domain.Company;
import com.zack.domain.Users;
import com.zack.enums.CompanyReviewStatus;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.feign.UserInfoMicroFeign;
import com.zack.inteceptor.JwtCurrentUserInteceptor;
import com.zack.service.CompanyService;
import com.zack.utils.JsonUtils;
import com.zack.vo.CompanyInfoVO;
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
    public CommonResult information(String hrUserId) {

        UsersVO hrUser = getHRInfoVO(hrUserId);

        CompanySimpleVO company = getCompany(hrUser.getHrInWhichCompanyId());

        return CommonResult.success(company);
    }

    private UsersVO getHRInfoVO(String hrUserId) {
        CommonResult commonResult = userInfoMicroFeign.get(hrUserId);
        Object data = commonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO hrUser = JsonUtils.jsonToPojo(json, UsersVO.class);
        return hrUser;
    }

    /**
     * saas获得企业基础信息
     * @return
     */
    @PostMapping("info")
    public CommonResult info() {

        Users currentUser = JwtCurrentUserInteceptor.currentUser.get();

        CompanySimpleVO companyInfo = getCompany(currentUser.getHrInWhichCompanyId());

        return CommonResult.success(companyInfo);
    }

    /**
     * saas获得查询企业详情
     * @return
     */
    @PostMapping("saas/moreInfo")
    public GraceJSONResult saasMoreInfo() {

        Users currentUser = JwtCurrentUserInteceptor.currentUser.get();

        CompanyInfoVO companyInfo = getCompanyMoreInfo(
                currentUser.getHrInWhichCompanyId());

        return GraceJSONResult.ok(companyInfo);
    }


    /**
     * app用户端获得查询企业详情
     * @return
     */
    @PostMapping("moreInfo")
    public GraceJSONResult moreInfo(String companyId) {
        CompanyInfoVO companyInfo = getCompanyMoreInfo(companyId);
        return GraceJSONResult.ok(companyInfo);
    }

    private CompanyInfoVO getCompanyMoreInfo(String companyId) {
        if (StrUtil.isBlank(companyId)) return null;

        String companyJson = redis.get(REDIS_COMPANY_MORE_INFO + ":" + companyId);
        if (StrUtil.isBlank(companyJson)) {
            // 查询数据库
            Company company = companyService.getById(companyId);
            if (company == null) {
                return null;
            }

            CompanyInfoVO infoVO = new CompanyInfoVO();
            BeanUtils.copyProperties(company, infoVO);

            redis.set(REDIS_COMPANY_MORE_INFO + ":" + companyId,
                    new Gson().toJson(infoVO),
                    1 * 60);
            return infoVO;
        } else {
            // 不为空，直接转换对象
            return new Gson().fromJson(companyJson, CompanyInfoVO.class);
        }
    }


    /**
     * 维护企业信息
     * @param companyInfoBO
     * @return
     */
    @PostMapping("modify")
    public CommonResult modify(
            @RequestBody ModifyCompanyInfoBO companyInfoBO,
            Integer num) throws Exception {

//        if (num!=null && num>1) {
//            Thread.sleep(5000);
//        }

        // 判断当前用户绑定的企业，是否和修改的企业一致，如果不一致，则异常
        checkUser(companyInfoBO.getCurrentUserId(), companyInfoBO.getCompanyId());

        // 修改企业信息
        companyService.modifyCompanyInfo(companyInfoBO, num);

        // 企业相册信息的保存
        // if (StrUtil.isNotBlank(companyInfoBO.getPhotos())) {
        //     companyService.savePhotos(companyInfoBO);
        // }

        return CommonResult.success();
    }

    /**
     * 校验企业下的HR是否OK
     * @param currentUserId
     * @param companyId
     */
    private void checkUser(String currentUserId, String companyId) {
        ThrowUtil.throwIf(StrUtil.isBlank(currentUserId),ErrorCode.COMPANY_INFO_UPDATED_ERROR);
        UsersVO hrUser = getHRInfoVO(currentUserId);
        ThrowUtil.throwIf(hrUser != null && !hrUser.getHrInWhichCompanyId().equalsIgnoreCase(companyId),ErrorCode.COMPANY_INFO_UPDATED_NO_AUTH_ERROR);
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

    /**
     * 根据企业id获得最新企业数据
     * @param companyId
     * @return
     */
    @PostMapping("admin/getCompanyInfo")
    public CommonResult getCompanyInfo(String companyId) {

        CompanyInfoVO companyInfo = companyService.getCompanyInfo(companyId);

        return CommonResult.success(companyInfo);
    }



    /**
     * 企业审核通过，用户成为HR角色
     * @param reviewCompanyBO
     * @return
     */
    @PostMapping("admin/doReview")
    public CommonResult getCompanyInfo(
            @RequestBody @Valid ReviewCompanyBO reviewCompanyBO) {

        // 1. 审核企业
        companyService.updateReviewInfo(reviewCompanyBO);

        // 2. 如果审核成功，则更新用户角色成为HR
        if (reviewCompanyBO.getReviewStatus() == CompanyReviewStatus.SUCCESSFUL.type) {
            userInfoMicroFeign.changeUserToHR(reviewCompanyBO.getHrUserId());
        }

        // 3. 清除用户端的企业缓存
        redis.del(REDIS_COMPANY_BASE_INFO + ":" + reviewCompanyBO.getCompanyId());

        return CommonResult.success();
    }
}

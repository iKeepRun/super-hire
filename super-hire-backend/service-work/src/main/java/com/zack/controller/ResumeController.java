package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.*;
import com.zack.common.CommonResult;
import com.zack.common.CommonResult;
import com.zack.common.CommonResult;
import com.zack.common.CommonResult;
import com.zack.domain.ResumeEducation;
import com.zack.domain.ResumeExpect;
import com.zack.domain.ResumeProjectExp;
import com.zack.domain.ResumeWorkExp;
import com.zack.service.ResumeService;
import com.zack.utils.GsonUtils;
import com.zack.vo.ResumeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/resume")
public class ResumeController extends BaseInfoProperties {
   @Autowired
   private ResumeService resumeService;

    @PostMapping("/init")
    public CommonResult init(@RequestParam("userId") String userId ){
        resumeService.initResume(userId);
        return CommonResult.success();
    }

    @GetMapping("/hello")
    public String update(){
        return "dada";
    }

    /**
     * 编辑简历信息
     * @param editResumeBO
     * @return
     */
    @PostMapping("modify")
    public CommonResult modify(@RequestBody @Valid EditResumeBO editResumeBO) {

        // TODO EditResumeBO 自行校验

        resumeService.modifyResume(editResumeBO);

        return CommonResult.success();
    }

    /**
     * 查询我的简历
     * @param userId
     * @return
     */
    @PostMapping("queryMyResume")
    public CommonResult queryMyResume(String userId) {

        if (StrUtil.isBlank(userId)) {
            return CommonResult.error("用户ID不能为空");
        }

        /**
         * 由于自己的简历在修改完毕以后，一般不会去做太多的修改，
         * 而且每次查询又是会涉及到多张表查询，内容信息比较多也比较大，
         * 所以可以采用redis进行信息存储，来提升查询的速度与性能。
         */

        String resumeJson = redis.get(REDIS_RESUME_INFO + ":" + userId);
        ResumeVO resumeVO = null;
        if (StrUtil.isBlank(resumeJson)) {
            resumeVO = resumeService.getResumeInfo(userId);
            redis.set(REDIS_RESUME_INFO + ":" + userId, GsonUtils.object2String(resumeVO));
        } else {
            resumeVO = GsonUtils.stringToBean(resumeJson, ResumeVO.class);
        }

        return CommonResult.success(resumeVO);
    }

    /**
     * 新增/编辑工作经验
     * @param editResumeBO
     * @return
     */
    @PostMapping("editWorkExp")
    public CommonResult editWorkExp(@RequestBody @Valid EditWorkExpBO editResumeBO) {
        // TODO EditWorkExpBO 自行校验
        resumeService.editWorkExp(editResumeBO);

        return CommonResult.success();
    }

    /**
     * 获得工作经验的详情
     * @param workExpId
     * @param userId
     * @return
     */
    @PostMapping("getWorkExp")
    public CommonResult getWorkExp(String workExpId, String userId) {

        if (StrUtil.isBlank(workExpId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("工作经验ID或用户ID不能为空");
        }

        ResumeWorkExp exp = resumeService.getWorkExp(workExpId, userId);

        return CommonResult.success(exp);
    }

    /**
     * 删除工作经验
     * @param workExpId
     * @param userId
     * @return
     */
    @PostMapping("deleteWorkExp")
    public CommonResult deleteWorkExp(String workExpId, String userId) {

        if (StrUtil.isBlank(workExpId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("工作经验ID或用户ID不能为空");
        }

        resumeService.deleteWorkExp(workExpId, userId);

        return CommonResult.success();
    }

    /**
     * 编辑项目经验
     * @param editResumeBO
     * @return
     */
    @PostMapping("editProjectExp")
    public CommonResult editProjectExp(@RequestBody @Valid EditProjectExpBO editResumeBO) {

        // TODO EditProjectExpBO 自行校验

        resumeService.editProjectExp(editResumeBO);

        return CommonResult.success();
    }

    /**
     * 查询项目经验的详情信息
     * @param projectExpId
     * @param userId
     * @return
     */
    @PostMapping("getProjectExp")
    public CommonResult getProjectExp(String projectExpId, String userId) {

        if (StrUtil.isBlank(projectExpId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("项目经验ID或用户ID不能为空");
        }

        ResumeProjectExp exp = resumeService.getProjectExp(projectExpId, userId);

        return CommonResult.success(exp);
    }

    @PostMapping("deleteProjectExp")
    public CommonResult deleteProjectExp(String projectExpId, String userId) {

        if (StrUtil.isBlank(projectExpId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("项目经验ID或用户ID不能为空");
        }

        resumeService.deleteProjectExp(projectExpId, userId);

        return CommonResult.success();
    }


    /**
     * 新增或修改我的学历
     * @param educationBO
     * @return
     */
    @PostMapping("editEducation")
    public CommonResult editEducation(@RequestBody @Valid EditEducationBO educationBO) {

        // TODO EditEducationBO 自行校验

        resumeService.editEducation(educationBO);

        return CommonResult.success();
    }

    /**
     * 获得教育经历详情
     * @param eduId
     * @param userId
     * @return
     */
    @PostMapping("getEducation")
    public CommonResult getEducation(String eduId, String userId) {

        if (StrUtil.isBlank(eduId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("教育经历ID或用户ID不能为空");
        }

        ResumeEducation education = resumeService.getEducation(eduId, userId);

        return CommonResult.success(education);
    }

    /**
     * 删除教育经历
     * @param eduId
     * @param userId
     * @return
     */
    @PostMapping("deleteEducation")
    public CommonResult deleteEducation(String eduId, String userId) {

        if (StrUtil.isBlank(eduId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("教育经历ID或用户ID不能为空");
        }

        resumeService.deleteEducation(eduId, userId);

        return CommonResult.success();
    }

    /**
     * 新增或者修改求职期望
     * @param expectBO
     * @return
     */
    @PostMapping("editJobExpect")
    public CommonResult editJobExpect(@RequestBody @Valid EditResumeExpectBO expectBO) {

        // TODO EditResumeExpectBO 自行校验

        resumeService.editJobExpect(expectBO);

        return CommonResult.success();
    }

    /**
     * 查询求职期望列表
     * @param resumeId
     * @param userId
     * @return
     */
    @PostMapping("getMyResumeExpectList")
    public CommonResult getMyResumeExpectList(String resumeId, String userId) {

        if (StrUtil.isBlank(resumeId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("简历ID或用户ID不能为空");
        }

        String myResumeExpectListJson = redis.get(REDIS_RESUME_EXPECT + ":" + userId);
        List<ResumeExpect> expectList = null;
        if (StrUtil.isBlank(myResumeExpectListJson)) {
            expectList = resumeService.getMyResumeExpectList(resumeId, userId);
        } else {
            expectList = GsonUtils.stringToList(myResumeExpectListJson, ResumeExpect.class);
        }

        return CommonResult.success(expectList);
    }

    /**
     * 删除求职期望
     * @param resumeExpectId
     * @param userId
     * @return
     */
    @PostMapping("deleteMyResumeExpect")
    public CommonResult deleteMyResumeExpect(String resumeExpectId, String userId) {

        if (StrUtil.isBlank(resumeExpectId) || StrUtil.isBlank(userId)) {
            return CommonResult.error("求职期望ID或用户ID不能为空");
        }

        resumeService.deleteResumeExpect(resumeExpectId, userId);

        return CommonResult.success();
    }
}

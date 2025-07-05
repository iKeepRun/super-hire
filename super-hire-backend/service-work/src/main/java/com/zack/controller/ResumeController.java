package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.EditResumeBO;
import com.zack.bo.EditWorkExpBO;
import com.zack.common.CommonResult;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.domain.ResumeWorkExp;
import com.zack.service.ResumeService;
import com.zack.utils.GsonUtils;
import com.zack.vo.ResumeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
}

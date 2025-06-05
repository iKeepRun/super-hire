package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;

import com.zack.common.GraceJSONResult;
import com.zack.domain.Users;
import com.zack.dto.ModifyUserDTO;
import com.zack.service.UsersService;
import com.zack.utils.JWTUtils;
import com.zack.vo.UsersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userinfo")
public class UserInfoController extends BaseInfoProperties {
    @Autowired
    private UsersService usersService;
    @Autowired
    private JWTUtils jwtUtils;

    @RequestMapping("/hello")
    public String hello(){
        return "hello user";
    }

    @PostMapping("modify")
    public CommonResult<UsersVO> modifyUserInfo(@RequestBody ModifyUserDTO modifyUserDTO){
        usersService.modifyUserInfo(modifyUserDTO);
        UsersVO usersVO =getUserInfo(modifyUserDTO.getUserId(), true);

        return CommonResult.success(usersVO);
    }


    private UsersVO getUserInfo(String userId, boolean needJWT) {
        // 查询获得用户的最新信息
        Users latestUser = usersService.getById(userId);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);

        if (needJWT) {
            // 重新生成并且覆盖原来的token
            String uToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(latestUser),
                    TOKEN_USER_PREFIX);
            usersVO.setUserToken(uToken);
        }
        return usersVO;
    }

    /**
     * 根据企业id，查询绑定的hr数量有多少
     * @param companyId
     * @return
     */
    @PostMapping("getCountsByCompanyId")
    public CommonResult getCountsByCompanyId(
            @RequestParam("companyId") String companyId) {

        String hrCountsStr = redis.get(REDIS_COMPANY_HR_COUNTS + ":" + companyId);
        Long hrCounts = 0l;
        if (StrUtil.isBlank(hrCountsStr)) {

            hrCounts = usersService.getCountsByCompanyId(companyId);
            redis.set(REDIS_COMPANY_HR_COUNTS + ":" + companyId,
                    hrCounts + "",
                    1 * 60);
            // FIXME: 此处有缓存击穿的风险，思考结合业务，怎么处理更好？
        } else {
            hrCounts = Long.valueOf(hrCountsStr);
        }

        return CommonResult.success(hrCounts);
    }


    /**
     * 绑定企业和hr用户的关系
     * @param hrUserId
     * @param realname
     * @param companyId
     * @return
     */
    @PostMapping("bindingHRToCompany")
    public CommonResult bindingHRToCompany(
            @RequestParam("hrUserId") String hrUserId,
            @RequestParam("realname") String realname,
            @RequestParam("companyId") String companyId) {

        usersService.updateUserCompanyId(hrUserId,
                realname,
                companyId);

        Users hrUser = usersService.getById(hrUserId);

        return CommonResult.success(hrUser.getMobile());
    }


    /**
     * 刷新用户信息，传递最新的用户信息以及刷新token给前端
     * @param userId
     * @return
     */
    @PostMapping("freshUserInfo")
    public CommonResult freshUserInfo(@RequestParam("userId") String userId) {
        UsersVO usersVO = getUserInfo(userId, true);
        return CommonResult.success(usersVO);
    }

}

package com.zack.controller;

import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;
import com.zack.domain.Users;
import com.zack.dto.ModifyUserDTO;
import com.zack.service.UsersService;
import com.zack.utils.JWTUtils;
import com.zack.vo.UsersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

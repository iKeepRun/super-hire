
//	此资源由 58学课资源站 收集整理
//	想要获取完整课件资料 请访问：58xueke.com
//	百万资源 畅享学习
package com.zack.dto;

import com.zack.exceptions.BusinessException;
import com.zack.exceptions.ErrorCode;
import com.zack.ar.AdminAR;
import com.zack.utils.MD5Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResetPwdDTO {

    private String adminId;
    private String password;
    private String rePassword;

    private void validate() {
        checkPwd();
        checkAdminId();
    }

    private void checkAdminId() {
        if (StringUtils.isBlank(adminId)){
            throw  new BusinessException(ErrorCode.ADMIN_NOT_EXIST);
        }

        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);

        adminAR = adminAR.selectById();
        if (adminAR == null){
            throw  new BusinessException(ErrorCode.ADMIN_NOT_EXIST);
        }
    }

    private void checkPwd() {
        if (StringUtils.isBlank(password)){
            throw new BusinessException(ErrorCode.ADMIN_PASSWORD_NULL_ERROR);
        }
        if (StringUtils.isBlank(rePassword)){
            throw new BusinessException(ErrorCode.ADMIN_PASSWORD_NULL_ERROR);
        }
        if (!password.equalsIgnoreCase(rePassword)){
            throw new BusinessException(ErrorCode.ADMIN_PASSWORD_ERROR);
        }
    }

    public void modifyPwd() {
        // 校验
        validate();

        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);

        // 重置密码
        // 生成随机数字或者英文字母结合的盐
        String slat = (int)((Math.random() * 9 + 1) * 100000) + "";
        String pwd = MD5Utils.encrypt(password, slat);
        adminAR.setPassword(pwd);
        adminAR.setSlat(slat);

        adminAR.setUpdated_time(new Date());

        adminAR.updateById();
    }

}

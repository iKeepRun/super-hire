package com.zack.controller;

import cn.hutool.core.util.StrUtil;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.common.ResponseStatusEnum;
import com.zack.config.MinIOConfig;
import com.zack.config.MinIOUtils;
import com.zack.dto.Base64FileDTO;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.utils.Base64ToFile;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController extends BaseInfoProperties {
    @Autowired
    private MinIOConfig minIOConfig;
    private static final String host = "http://192.168.1.104:8000/static/face/";

    @GetMapping("hello")
    public String hello() {
        return "hello file";
    }


    @PostMapping("uploadFace1")
    public CommonResult uploadFace1(@RequestParam("file") MultipartFile file,
                                   @RequestParam("userId") String userId,
                                   HttpServletRequest request) throws IOException {
        //获取文件的原始名字
        String originalFilename = file.getOriginalFilename();
        //获取文件的后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = userId + suffix;

        //获取文件的存储路径
        String fileUrl = "/Users/chenzhiqiang/Downloads/temp" + File.separator + "face" + File.separator + fileName;
        File fileNew = new File(fileUrl);
        //如果文件夹不存在，就创建
        if (!fileNew.getParentFile().exists()) {
            log.info("文件夹不存在，创建文件夹");
            //创建多级目录
            fileNew.getParentFile().mkdirs();
        }

        //上传文件到服务器
        file.transferTo(fileNew);

        //返回结果
        String localUrl = host + fileName;
        log.info("生成的路径{}", localUrl);
        return CommonResult.success(localUrl);
    }


    @PostMapping("uploadFace")
    public CommonResult uploadFace(@RequestParam("file") MultipartFile file,
                                   @RequestParam("userId") String userId) {

        ThrowUtil.throwIf(StrUtil.isBlank(userId), ErrorCode.PARAMS_ERROR);
        //获取文件的原始名字
        String originalFilename = file.getOriginalFilename();
        ThrowUtil.throwIf(StrUtil.isBlank(originalFilename), ErrorCode.PARAMS_ERROR);
        String objectName= userId + File.separator+originalFilename;
        try {
            String url = MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, file.getInputStream(), true);
            return CommonResult.success(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("uploadAdminFace")
    public CommonResult uploadAdminFace(@RequestBody Base64FileDTO base64FileDTO) throws Exception {
        String suffix = ".png";
        String uuid= UUID.randomUUID().toString().replace("-", "");
        String objectName= uuid + suffix;

        String fileUrl = "/Users/chenzhiqiang/Downloads/temp"+File.separator+"adminFace"+File.separator + objectName;

        Base64ToFile.Base64ToFile(base64FileDTO.getBase64File(),fileUrl);

        //使用minio上传
        MinIOUtils.uploadFile(minIOConfig.getBucketName(),objectName,fileUrl);
        String url=minIOConfig.getFileHost()+"/"+minIOConfig.getBucketName()+"/"+objectName;
        log.info("上传之后生成的路径{}", url);
        //删除临时文件
        // File file = new File(fileUrl);
        // if (file.exists()) { // 如果文件存在
        //     file.delete(); // 删除文件
        // } else {
        //     log.info("文件不存在");
        // }
        return CommonResult.success(url);
    }


    @PostMapping("uploadLogo")
    public CommonResult uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StrUtil.isBlank(filename)) {
            return CommonResult.error(ErrorCode.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/logo/" + dealFilename(filename);
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);
        return CommonResult.success(imageUrl);
    }

    @PostMapping("uploadBizLicense")
    public CommonResult uploadBizLicense(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StrUtil.isBlank(filename)) {
            return CommonResult.error(ErrorCode.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/bizLicense/" + dealFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);
        return CommonResult.success(imageUrl);
    }


    private String dealFilename(String filename) {
        String suffixName = filename.substring(filename.lastIndexOf("."));
        String fName = filename.substring(0, filename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return fName + "-" + uuid + suffixName;
    }
}

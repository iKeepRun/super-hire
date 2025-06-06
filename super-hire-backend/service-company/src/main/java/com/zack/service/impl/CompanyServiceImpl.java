package com.zack.service.impl;

import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zack.base.BaseInfoProperties;
import com.zack.bo.CreateCompanyBO;
import com.zack.bo.ModifyCompanyInfoBO;
import com.zack.bo.QueryCompanyBO;
import com.zack.bo.ReviewCompanyBO;
import com.zack.common.CommonPage;
import com.zack.domain.Company;
import com.zack.domain.CompanyPhoto;
import com.zack.enums.CompanyReviewStatus;
import com.zack.enums.YesOrNo;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.mapper.CompanyMapperCustom;
import com.zack.mapper.CompanyPhotoMapper;
import com.zack.service.CompanyService;
import com.zack.mapper.CompanyMapper;
import com.zack.vo.CompanyInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenzhiqiang
 * @description 针对表【company(企业表)】的数据库操作Service实现
 * @createDate 2025-05-17 10:19:37
 */
@Service
public class CompanyServiceImpl extends BaseInfoProperties implements CompanyService {
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private CompanyMapperCustom companyMapperCustom;
    @Autowired
    private CompanyPhotoMapper companyPhotoMapper;
    @Override
    public Company getByFullName(String fullName) {
        Company tempCompany = companyMapper.selectOne(
                new QueryWrapper<Company>()
                        .eq("company_name", fullName)
        );

        return tempCompany;
    }


    @Transactional
    @Override
    public String createNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();

        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setIsVip(YesOrNo.NO.type);
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setCreatedTime(LocalDateTime.now());
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.insert(newCompany);

        return newCompany.getId();
    }

    @Transactional
    @Override
    public String resetNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();

        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setId(createCompanyBO.getCompanyId());
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(newCompany);

        return createCompanyBO.getCompanyId();
    }

    @Override
    public Company getById(String id) {
        return companyMapper.selectById(id);
    }

    @Transactional
    @Override
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());

        pendingCompany.setReviewStatus(CompanyReviewStatus.REVIEW_ING.type);
        pendingCompany.setReviewReplay(""); // 如果有内容，则重置覆盖之前的审核意见
        pendingCompany.setAuthLetter(reviewCompanyBO.getAuthLetter());

        pendingCompany.setCommitUserId(reviewCompanyBO.getHrUserId());
        pendingCompany.setCommitUserMobile(reviewCompanyBO.getHrMobile());
        pendingCompany.setCommitDate(LocalDate.now());

        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }


    @Override
    public CommonPage<CompanyInfoVO> queryCompanyList(QueryCompanyBO companyBO,
                                                Integer page,
                                                Integer limit){
        PageHelper.startPage(page, limit);

        Map<String,Object> map=new HashMap<>();
        map.put("companyName", companyBO.getCompanyName());
        map.put("realName", companyBO.getCommitUser());
        map.put("reviewStatus", companyBO.getReviewStatus());
        map.put("commitDateStart", companyBO.getCommitDateStart());
        map.put("commitDateEnd", companyBO.getCommitDateEnd());


        List<CompanyInfoVO> companyInfoVOList = companyMapperCustom.queryCompanyList(map);

        return setPage(companyInfoVOList,page);
    }


    @Override
    public CompanyInfoVO getCompanyInfo(String companyId) {

        Map<String, Object> map = new HashMap<>();
        map.put("companyId", companyId);

        CompanyInfoVO companyInfo = companyMapperCustom.getCompanyInfo(map);
        return companyInfo;
    }


    @Transactional
    @Override
    public void updateReviewInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());

        pendingCompany.setReviewStatus(reviewCompanyBO.getReviewStatus());
        pendingCompany.setReviewReplay(reviewCompanyBO.getReviewReplay());

        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }

    @Override
    public void modifyCompanyInfo(ModifyCompanyInfoBO companyInfoBO, Integer num) {
        String companyId= companyInfoBO.getCompanyId();

        ThrowUtil.throwIf(StrUtil.isBlank(companyId), ErrorCode.PARAMS_ERROR);

        Company penddingCompany = new Company();
        penddingCompany.setId(companyInfoBO.getCompanyId());
        penddingCompany.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(companyInfoBO, penddingCompany);

        companyMapper.updateById(penddingCompany);
        //删除企业缓存信息
        redis.del(REDIS_COMPANY_BASE_INFO+":"+companyId);
        redis.del(REDIS_COMPANY_MORE_INFO+":"+companyId);

    }

    /**
     * 修改企业相册
     * @param modifyCompanyInfoBO
     */
    @Override
    public void savePhotos(ModifyCompanyInfoBO modifyCompanyInfoBO) {
        String companyId=modifyCompanyInfoBO.getCompanyId();
        CompanyPhoto companyPhoto=new CompanyPhoto();
        companyPhoto.setCompanyId(companyId);
        companyPhoto.setPhotos(modifyCompanyInfoBO.getPhotos());

        //判断是否有企业相册
        CompanyPhoto tempPhoto = getPhotos(companyId);
        if (tempPhoto==null){
            companyPhotoMapper.insert(companyPhoto);
        }else{
            companyPhotoMapper.update(companyPhoto,new UpdateWrapper<CompanyPhoto>().eq(
                    "company_id",companyId
            ));
        }
    }

    @Override
    public CompanyPhoto getPhotos(String companyId) {
        return companyPhotoMapper.selectOne(new QueryWrapper<CompanyPhoto>().eq("company_id", companyId));
    }
}





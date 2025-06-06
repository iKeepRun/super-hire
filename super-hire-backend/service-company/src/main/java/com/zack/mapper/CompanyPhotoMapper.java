package com.zack.mapper;

import com.zack.domain.CompanyPhoto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author chenzhiqiang
* @description 针对表【company_photo(企业相册表，本表只存企业上传的图片)】的数据库操作Mapper
* @createDate 2025-05-17 10:19:37
* @Entity com.zack.domain.CompanyPhoto
*/
@Mapper
public interface CompanyPhotoMapper extends BaseMapper<CompanyPhoto> {

}





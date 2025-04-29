package com.zack.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zack.domain.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
* @author mczq
* @description 针对表【users(用户表)】的数据库操作Mapper
* @createDate 2025-04-21 13:42:40
* @Entity generator.domain.Users
*/
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}





package com.zack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zack.domain.Industry;
import com.zack.mapper.IndustryMapperCustom;
import com.zack.service.IndustryService;
import com.zack.mapper.IndustryMapper;
import com.zack.vo.TopIndustryWithThirdListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author chenzhiqiang
* @description 针对表【industry(行业表)】的数据库操作Service实现
* @createDate 2025-05-17 10:19:37
*/
@Service
public class IndustryServiceImpl extends ServiceImpl<IndustryMapper, Industry>
    implements IndustryService{
    @Autowired
    private IndustryMapperCustom industryMapperCustom;
    @Override
    public boolean getIndustryIsExistByName(String nodeName) {
        Industry Industry = baseMapper.selectOne(new QueryWrapper<Industry>()
                .eq("name", nodeName));

        return Industry != null ? true : false;
    }



    @Transactional
    @Override
    public void createIndustry(Industry industry) {
        baseMapper.insert(industry);
    }


    @Override
    public List<Industry> getTopIndustryList() {
        return getChildrenIndustryList("0");
    }

    @Override
    public List<Industry> getChildrenIndustryList(String industryId) {

        List<Industry> list = baseMapper.selectList(new QueryWrapper<Industry>()
                .eq("father_id", industryId)
                .orderByAsc("sort")
        );

        return list;
    }

    @Transactional
    @Override
    public void updateIndustry(Industry industry) {
        baseMapper.updateById(industry);
    }

    @Override
    public Long getChildrenIndustryCounts(String industryId) {

        Long counts = baseMapper.selectCount(new QueryWrapper<Industry>()
                .eq("father_id", industryId)
        );

        return counts;
    }

    @Override
    public List<Industry> getThirdListByTop(String topIndustryId) {
        Map<String,Object> map=new HashMap<>();
        map.put("topIndustryId", topIndustryId);
        return industryMapperCustom.getThirdIndustryByTop(map);
    }

    @Override
    public String getTopIndustryId(String thirdIndustryId) {
        Map<String, Object> map = new HashMap<>();
        map.put("thirdIndustryId", thirdIndustryId);

        return industryMapperCustom.getTopIndustryId(map);
    }

    @Override
    public List<TopIndustryWithThirdListVO> getAllThirdIndustryList() {
        return industryMapperCustom.getAllThirdIndustryList();
    }
}





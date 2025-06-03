package com.zack.service;

import com.zack.domain.Industry;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zack.vo.TopIndustryWithThirdListVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chenzhiqiang
 * @description 针对表【industry(行业表)】的数据库操作Service
 * @createDate 2025-05-17 10:19:37
 */
@Mapper
public interface IndustryService extends IService<Industry> {
    /**
     * 根据名称判断是否存在
     *
     * @param nodeName
     * @return
     */
    boolean getIndustryIsExistByName(String nodeName);

    void createIndustry(Industry industry);

    void updateIndustry(Industry industry);

    /**
     * 获得所有顶级（一级）分类列表
     *
     * @return
     */
    List<Industry> getTopIndustryList();

    List<Industry> getChildrenIndustryList(String industryId);

    Long getChildrenIndustryCounts(String industryId);
    List<Industry> getThirdListByTop(String topIndustryId);
    String getTopIndustryId(String thirdIndustryId);

    List<TopIndustryWithThirdListVO> getAllThirdIndustryList();
}

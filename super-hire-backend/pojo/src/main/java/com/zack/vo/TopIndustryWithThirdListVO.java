package com.zack.vo;

import com.zack.domain.Industry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TopIndustryWithThirdListVO {
    private String topId;
    private List<Industry> thirdIndustryList;
}

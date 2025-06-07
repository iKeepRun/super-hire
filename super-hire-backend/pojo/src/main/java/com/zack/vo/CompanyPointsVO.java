package com.zack.vo;


import com.zack.domain.DataDictionary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompanyPointsVO {

    private List<DataDictionary> advantageList;
    private List<DataDictionary> benefitsList;
    private List<DataDictionary> bonusList;
    private List<DataDictionary> subsidyList;
}

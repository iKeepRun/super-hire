package com.zack.vo;

import com.zack.domain.ResumeEducation;
import com.zack.domain.ResumeProjectExp;
import com.zack.domain.ResumeWorkExp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResumeVO {

    private String id;
    private String userId;
    private String advantage;
    private String advantageHtml;
    private String credentials;
    private String skills;
    private String status;
    private LocalDateTime refreshTime;

    private List<ResumeWorkExp> workExpList;
    private List<ResumeProjectExp> projectExpList;
    private List<ResumeEducation> educationList;

}

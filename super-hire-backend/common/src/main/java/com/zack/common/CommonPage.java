package com.zack.common;

import lombok.Data;

import java.util.List;
@Data
public class CommonPage {

    private int page;			// 当前页数
    private long total;			// 总页数
    private long records;		// 总记录数
    private List<?> rows;		// 每行显示的内容

}

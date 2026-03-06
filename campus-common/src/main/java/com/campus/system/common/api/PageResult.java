package com.campus.system.common.api;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页数据包装类
 */
@Data
public class PageResult<T> implements Serializable {

    private Long total;
    private List<T> list;
    private Long pageNum;
    private Long pageSize;

    public PageResult() {
    }

    public PageResult(Long total, List<T> list, Long pageNum, Long pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}

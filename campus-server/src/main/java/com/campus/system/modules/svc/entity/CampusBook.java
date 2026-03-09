package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图书信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_book")
public class CampusBook extends BaseEntity {

    /** 书名 */
    private String bookName;

    /** 作者 */
    private String author;

    /** ISBN编号 */
    private String isbn;

    /** 出版社 */
    private String publisher;

    /** 分类 */
    private String category;

    /** 馆藏总量 */
    private Integer totalCount;

    /** 可借数量 */
    private Integer availableCount;

    /** 存放位置 */
    private String location;
}

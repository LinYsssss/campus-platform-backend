package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教学课件资料表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_course_material")
public class EduCourseMaterial extends BaseEntity {

    /** 课程ID */
    private Long courseId;

    /** 上传者ID（教师） */
    private Long uploadUserId;

    /** 文件原始名称 */
    private String fileName;

    /** 存储路径 */
    private String filePath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件MIME类型 */
    private String fileType;

    /** 文件MD5（防重复上传） */
    private String fileMd5;

    /** 下载次数 */
    private Integer downloadCount;
}

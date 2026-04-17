package com.campus.system.modules.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.system.modules.edu.entity.EduScore;
import org.springframework.web.multipart.MultipartFile;

public interface IEduScoreService extends IService<EduScore> {

    /**
     * Excel 批量导入成绩。
     * 表头要求：学号 | 成绩 | 成绩类型(百分制/等级制) | 等级
     * 成绩类型为百分制时校验 0~100；为等级制时取等级列。
     *
     * @param file     Excel 文件
     * @param courseId 课程ID
     * @param semester 学期（如 2025-2026-1）
     * @return 导入结果描述
     */
    String importScores(MultipartFile file, Long courseId, String semester);
}

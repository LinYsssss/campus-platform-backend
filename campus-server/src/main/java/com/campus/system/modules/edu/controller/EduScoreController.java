package com.campus.system.modules.edu.controller;

import com.campus.system.modules.edu.service.IEduScoreService;
import com.campus.system.modules.edu.service.IEduScoreAppealService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 成绩管理控制器（合并成绩录入 + 申诉复议）
 */
@RestController
@RequestMapping("/edu/score")
@RequiredArgsConstructor
public class EduScoreController {

    private final IEduScoreService eduScoreService;
    private final IEduScoreAppealService eduScoreAppealService;
}

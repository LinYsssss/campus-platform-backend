package com.campus.system.modules.edu.controller;

import com.campus.system.modules.edu.service.IEduCourseEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程评价控制器
 */
@RestController
@RequestMapping("/edu/evaluation")
@RequiredArgsConstructor
public class EduEvaluationController {

    private final IEduCourseEvaluationService eduCourseEvaluationService;
}

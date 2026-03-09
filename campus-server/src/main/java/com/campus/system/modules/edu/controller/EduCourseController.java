package com.campus.system.modules.edu.controller;

import com.campus.system.modules.edu.service.IEduCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/edu/course")
@RequiredArgsConstructor
public class EduCourseController {

    private final IEduCourseService eduCourseService;
}

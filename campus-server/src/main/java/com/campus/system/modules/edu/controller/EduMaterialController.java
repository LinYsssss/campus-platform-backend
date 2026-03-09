package com.campus.system.modules.edu.controller;

import com.campus.system.modules.edu.service.IEduCourseMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教学资料/课件控制器
 */
@RestController
@RequestMapping("/edu/material")
@RequiredArgsConstructor
public class EduMaterialController {

    private final IEduCourseMaterialService eduCourseMaterialService;
}

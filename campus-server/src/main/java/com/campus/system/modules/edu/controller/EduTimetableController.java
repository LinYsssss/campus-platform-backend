package com.campus.system.modules.edu.controller;

import com.campus.system.modules.edu.service.IEduTimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课表排课控制器
 */
@RestController
@RequestMapping("/edu/timetable")
@RequiredArgsConstructor
public class EduTimetableController {

    private final IEduTimetableService eduTimetableService;
}

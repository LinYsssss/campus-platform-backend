package com.campus.system.modules.edu.controller;

import com.campus.system.modules.edu.service.IEduAttendanceSessionService;
import com.campus.system.modules.edu.service.IEduAttendanceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考勤签到控制器（合并场次管理 + 签到记录）
 */
@RestController
@RequestMapping("/edu/attendance")
@RequiredArgsConstructor
public class EduAttendanceController {

    private final IEduAttendanceSessionService eduAttendanceSessionService;
    private final IEduAttendanceRecordService eduAttendanceRecordService;
}

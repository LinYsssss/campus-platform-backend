package com.campus.system.modules.edu.controller;

import com.campus.system.modules.edu.service.IEduLeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请假管理控制器
 */
@RestController
@RequestMapping("/edu/leave")
@RequiredArgsConstructor
public class EduLeaveController {

    private final IEduLeaveRequestService eduLeaveRequestService;
}

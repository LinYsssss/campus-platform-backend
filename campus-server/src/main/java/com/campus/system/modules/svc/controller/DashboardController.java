package com.campus.system.modules.svc.controller;

import com.campus.system.modules.svc.service.ICampusDashboardSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 大屏看板数据控制器
 */
@RestController
@RequestMapping("/svc/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ICampusDashboardSnapshotService campusDashboardSnapshotService;
}

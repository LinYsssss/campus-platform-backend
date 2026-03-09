package com.campus.system.modules.svc.controller;

import com.campus.system.modules.svc.service.ICampusDormitoryBuildingService;
import com.campus.system.modules.svc.service.ICampusDormitoryRoomService;
import com.campus.system.modules.svc.service.ICampusDormitoryAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 宿舍管理控制器（合并楼栋 + 房间 + 分配）
 */
@RestController
@RequestMapping("/svc/dormitory")
@RequiredArgsConstructor
public class DormitoryController {

    private final ICampusDormitoryBuildingService dormitoryBuildingService;
    private final ICampusDormitoryRoomService dormitoryRoomService;
    private final ICampusDormitoryAllocationService dormitoryAllocationService;
}

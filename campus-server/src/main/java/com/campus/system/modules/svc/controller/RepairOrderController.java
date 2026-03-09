package com.campus.system.modules.svc.controller;

import com.campus.system.modules.svc.service.ICampusRepairOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报修工单控制器
 */
@RestController
@RequestMapping("/svc/repair")
@RequiredArgsConstructor
public class RepairOrderController {

    private final ICampusRepairOrderService campusRepairOrderService;
}

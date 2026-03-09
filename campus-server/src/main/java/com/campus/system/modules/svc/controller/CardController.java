package com.campus.system.modules.svc.controller;

import com.campus.system.modules.svc.service.ICampusCardRecordService;
import com.campus.system.modules.svc.service.ICampusCardLossService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 校园卡管理控制器（合并消费记录 + 挂失）
 */
@RestController
@RequestMapping("/svc/card")
@RequiredArgsConstructor
public class CardController {

    private final ICampusCardRecordService campusCardRecordService;
    private final ICampusCardLossService campusCardLossService;
}

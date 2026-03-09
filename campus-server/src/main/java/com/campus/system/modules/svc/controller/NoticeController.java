package com.campus.system.modules.svc.controller;

import com.campus.system.modules.svc.service.ICampusNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告管理控制器
 */
@RestController
@RequestMapping("/svc/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final ICampusNoticeService campusNoticeService;
}

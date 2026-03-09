package com.campus.system.modules.svc.controller;

import com.campus.system.modules.svc.service.ICampusBookService;
import com.campus.system.modules.svc.service.ICampusBookBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图书管理控制器（合并图书信息 + 借阅记录）
 */
@RestController
@RequestMapping("/svc/book")
@RequiredArgsConstructor
public class BookController {

    private final ICampusBookService campusBookService;
    private final ICampusBookBorrowService campusBookBorrowService;
}

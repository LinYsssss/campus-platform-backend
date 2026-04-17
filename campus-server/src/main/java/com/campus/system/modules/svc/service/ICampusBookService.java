package com.campus.system.modules.svc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.system.modules.svc.entity.CampusBook;

public interface ICampusBookService extends IService<CampusBook> {

    /**
     * 学生借阅一本书：校验逾期黑名单、重复借阅、库存后扣减库存并写入借阅记录。
     */
    void borrow(Long bookId, Long studentId);

    /**
     * 归还借阅记录：更新借阅状态并回补库存。
     */
    void returnBook(Long borrowId, Long currentUserId, boolean isAdmin);
}

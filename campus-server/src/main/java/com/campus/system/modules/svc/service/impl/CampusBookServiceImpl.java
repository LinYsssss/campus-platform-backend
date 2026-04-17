package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusBook;
import com.campus.system.modules.svc.entity.CampusBookBorrow;
import com.campus.system.modules.svc.mapper.CampusBookMapper;
import com.campus.system.modules.svc.service.ICampusBookBorrowService;
import com.campus.system.modules.svc.service.ICampusBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CampusBookServiceImpl extends ServiceImpl<CampusBookMapper, CampusBook> implements ICampusBookService {

    private final ICampusBookBorrowService borrowService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrow(Long bookId, Long studentId) {
        long overdueCount = borrowService.count(
                new LambdaQueryWrapper<CampusBookBorrow>()
                        .eq(CampusBookBorrow::getStudentId, studentId)
                        .eq(CampusBookBorrow::getStatus, 0)
                        .lt(CampusBookBorrow::getDueTime, LocalDateTime.now())
        );
        if (overdueCount > 0) {
            throw new BusinessException("您存在逾期未归还的图书，请先归还后再借阅");
        }

        long borrowing = borrowService.count(
                new LambdaQueryWrapper<CampusBookBorrow>()
                        .eq(CampusBookBorrow::getBookId, bookId)
                        .eq(CampusBookBorrow::getStudentId, studentId)
                        .eq(CampusBookBorrow::getStatus, 0)
        );
        if (borrowing > 0) throw new BusinessException("您已借阅此书且未归还");

        CampusBook book = this.getById(bookId);
        if (book == null) throw new BusinessException("图书不存在");
        if (book.getAvailableCount() <= 0) throw new BusinessException("该图书暂无可借库存");

        boolean inventoryUpdated = this.update(new LambdaUpdateWrapper<CampusBook>()
                .eq(CampusBook::getId, bookId)
                .gt(CampusBook::getAvailableCount, 0)
                .setSql("available_count = available_count - 1"));
        if (!inventoryUpdated) {
            throw new BusinessException("该图书暂无可借库存");
        }

        CampusBookBorrow borrow = new CampusBookBorrow();
        borrow.setBookId(bookId);
        borrow.setStudentId(studentId);
        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setDueTime(LocalDateTime.now().plusDays(30));
        borrow.setStatus(0);
        borrow.setOverdueDays(0);
        borrowService.save(borrow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long borrowId, Long currentUserId, boolean isAdmin) {
        CampusBookBorrow borrow = borrowService.getById(borrowId);
        if (borrow == null) throw new BusinessException("借阅记录不存在");
        if (borrow.getStatus() != 0 && borrow.getStatus() != 2) throw new BusinessException("该记录已归还");

        if (!currentUserId.equals(borrow.getStudentId()) && !isAdmin) {
            throw new BusinessException("无权归还该借阅记录");
        }

        borrow.setReturnTime(LocalDateTime.now());
        borrow.setStatus(1);
        if (LocalDateTime.now().isAfter(borrow.getDueTime())) {
            long days = ChronoUnit.DAYS.between(borrow.getDueTime(), LocalDateTime.now());
            borrow.setOverdueDays((int) days);
        }
        borrowService.updateById(borrow);

        this.update(new LambdaUpdateWrapper<CampusBook>()
                .eq(CampusBook::getId, borrow.getBookId())
                .setSql("available_count = available_count + 1"));
    }
}

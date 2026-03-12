package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusBook;
import com.campus.system.modules.svc.entity.CampusBookBorrow;
import com.campus.system.modules.svc.service.ICampusBookBorrowService;
import com.campus.system.modules.svc.service.ICampusBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 图书管理控制器（图书台账 + 借阅/归还）
 */
@RestController
@RequestMapping("/svc/book")
@RequiredArgsConstructor
public class CampusBookController {

    private final ICampusBookService bookService;
    private final ICampusBookBorrowService borrowService;

    // ============ 图书台账 ============

    /** 分页查询图书 */
    @GetMapping("/page")
    public Result<PageResult<CampusBook>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {

        LambdaQueryWrapper<CampusBook> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(CampusBook::getBookName, keyword).or().like(CampusBook::getAuthor, keyword).or().like(CampusBook::getIsbn, keyword));
        }
        if (StrUtil.isNotBlank(category)) wrapper.eq(CampusBook::getCategory, category);
        wrapper.orderByDesc(CampusBook::getId);

        Page<CampusBook> page = bookService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /** 新增图书 */
    @PostMapping
    @SaCheckPermission("svc:book:add")
    @LogRecord(module = "图书管理", type = "新增")
    public Result<Void> add(@RequestBody CampusBook book) {
        book.setAvailableCount(book.getTotalCount());
        bookService.save(book);
        return Result.success();
    }

    /** 更新图书 */
    @PutMapping
    @SaCheckPermission("svc:book:edit")
    public Result<Void> update(@RequestBody CampusBook book) {
        bookService.updateById(book);
        return Result.success();
    }

    /** 删除图书 */
    @DeleteMapping("/{id}")
    @SaCheckPermission("svc:book:delete")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.success();
    }

    // ============ 借阅管理 ============

    /** 借书（自动扣减可借数量，默认借期30天） */
    @PostMapping("/borrow")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> borrow(@RequestParam Long bookId) {
        Long studentId = StpUtil.getLoginIdAsLong();

        // 检查是否已借未还
        long borrowing = borrowService.count(
                new LambdaQueryWrapper<CampusBookBorrow>()
                        .eq(CampusBookBorrow::getBookId, bookId)
                        .eq(CampusBookBorrow::getStudentId, studentId)
                        .eq(CampusBookBorrow::getStatus, 0)
        );
        if (borrowing > 0) throw new BusinessException("您已借阅此书且未归还");

        CampusBook book = bookService.getById(bookId);
        if (book == null) throw new BusinessException("图书不存在");
        if (book.getAvailableCount() <= 0) throw new BusinessException("该图书暂无可借库存");

        // 入库借阅记录
        CampusBookBorrow borrow = new CampusBookBorrow();
        borrow.setBookId(bookId);
        borrow.setStudentId(studentId);
        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setDueTime(LocalDateTime.now().plusDays(30));
        borrow.setStatus(0); // 借阅中
        borrow.setOverdueDays(0);
        borrowService.save(borrow);

        // 扣减可借数量
        bookService.update(new LambdaUpdateWrapper<CampusBook>()
                .eq(CampusBook::getId, bookId)
                .setSql("available_count = available_count - 1"));
        return Result.success();
    }

    /** 还书 */
    @PutMapping("/return/{borrowId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> returnBook(@PathVariable Long borrowId) {
        CampusBookBorrow borrow = borrowService.getById(borrowId);
        if (borrow == null) throw new BusinessException("借阅记录不存在");
        if (borrow.getStatus() != 0 && borrow.getStatus() != 2) throw new BusinessException("该记录已归还");

        borrow.setReturnTime(LocalDateTime.now());
        borrow.setStatus(1); // 已归还

        // 计算逾期天数
        if (LocalDateTime.now().isAfter(borrow.getDueTime())) {
            long days = ChronoUnit.DAYS.between(borrow.getDueTime(), LocalDateTime.now());
            borrow.setOverdueDays((int) days);
        }
        borrowService.updateById(borrow);

        // 归还后增加可借数量
        bookService.update(new LambdaUpdateWrapper<CampusBook>()
                .eq(CampusBook::getId, borrow.getBookId())
                .setSql("available_count = available_count + 1"));
        return Result.success();
    }

    /** 我的借阅记录 */
    @GetMapping("/borrow/my")
    public Result<PageResult<CampusBookBorrow>> myBorrows(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long studentId = StpUtil.getLoginIdAsLong();
        Page<CampusBookBorrow> page = borrowService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<CampusBookBorrow>()
                        .eq(CampusBookBorrow::getStudentId, studentId)
                        .orderByDesc(CampusBookBorrow::getId)
        );
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /** 管理员查询全部借阅记录 */
    @GetMapping("/borrow/page")
    @SaCheckPermission("svc:book:list")
    public Result<PageResult<CampusBookBorrow>> borrowPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<CampusBookBorrow> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(CampusBookBorrow::getStatus, status);
        wrapper.orderByDesc(CampusBookBorrow::getId);
        Page<CampusBookBorrow> page = borrowService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }
}

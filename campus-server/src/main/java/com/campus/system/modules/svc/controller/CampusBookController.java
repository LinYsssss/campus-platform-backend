package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.modules.svc.entity.CampusBook;
import com.campus.system.modules.svc.entity.CampusBookBorrow;
import com.campus.system.modules.svc.service.ICampusBookBorrowService;
import com.campus.system.modules.svc.service.ICampusBookService;
import com.campus.system.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 图书管理控制器（图书台账 + 借阅/归还）
 */
@RestController
@RequestMapping("/svc/book")
@RequiredArgsConstructor
@Tag(name = "图书管理", description = "图书台账与借阅归还接口")
public class CampusBookController {

    private final ICampusBookService bookService;
    private final ICampusBookBorrowService borrowService;

    @GetMapping("/page")
    @Operation(summary = "分页查询图书列表")
    public Result<PageResult<CampusBook>> page(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "图书分类") @RequestParam(required = false) String category) {

        LambdaQueryWrapper<CampusBook> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(CampusBook::getBookName, keyword)
                    .or().like(CampusBook::getAuthor, keyword)
                    .or().like(CampusBook::getIsbn, keyword));
        }
        if (StrUtil.isNotBlank(category)) wrapper.eq(CampusBook::getCategory, category);
        wrapper.orderByDesc(CampusBook::getId);

        Page<CampusBook> page = bookService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    @PostMapping
    @SaCheckPermission("svc:book:add")
    @LogRecord(module = "图书管理", type = "新增")
    @Operation(summary = "新增图书")
    public Result<Void> add(@RequestBody CampusBook book) {
        book.setAvailableCount(book.getTotalCount());
        bookService.save(book);
        return Result.success();
    }

    @PutMapping
    @SaCheckPermission("svc:book:edit")
    @Operation(summary = "更新图书")
    public Result<Void> update(@RequestBody CampusBook book) {
        bookService.updateById(book);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("svc:book:delete")
    @Operation(summary = "删除图书")
    public Result<Void> delete(@Parameter(description = "图书ID") @PathVariable Long id) {
        bookService.removeById(id);
        return Result.success();
    }

    @PostMapping("/borrow")
    @Operation(summary = "提交图书借阅")
    public Result<Void> borrow(@Parameter(description = "图书ID") @RequestParam Long bookId) {
        bookService.borrow(bookId, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PutMapping("/return/{borrowId}")
    @Operation(summary = "归还图书")
    public Result<Void> returnBook(@Parameter(description = "借阅记录ID") @PathVariable Long borrowId) {
        bookService.returnBook(borrowId, SecurityUtils.getCurrentUserId(), SecurityUtils.hasRole("admin"));
        return Result.success();
    }

    @GetMapping("/borrow/my")
    @Operation(summary = "查询我的借阅记录")
    public Result<PageResult<CampusBookBorrow>> myBorrows(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long studentId = SecurityUtils.getCurrentUserId();
        Page<CampusBookBorrow> page = borrowService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<CampusBookBorrow>()
                        .eq(CampusBookBorrow::getStudentId, studentId)
                        .orderByDesc(CampusBookBorrow::getId)
        );
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    @GetMapping("/borrow/page")
    @SaCheckPermission("svc:book:list")
    @Operation(summary = "分页查询借阅记录")
    public Result<PageResult<CampusBookBorrow>> borrowPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "借阅状态") @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<CampusBookBorrow> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(CampusBookBorrow::getStatus, status);
        wrapper.orderByDesc(CampusBookBorrow::getId);
        Page<CampusBookBorrow> page = borrowService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }
}

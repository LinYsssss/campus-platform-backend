package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusNotice;
import com.campus.system.modules.svc.entity.CampusNoticeRead;
import com.campus.system.modules.svc.service.ICampusNoticeReadService;
import com.campus.system.modules.svc.service.ICampusNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 通知公告控制器
 */
@RestController
@RequestMapping("/svc/notice")
@RequiredArgsConstructor
public class CampusNoticeController {

    private final ICampusNoticeService noticeService;
    private final ICampusNoticeReadService noticeReadService;

    /** 分页查询公告（已发布） */
    @GetMapping("/page")
    public Result<PageResult<CampusNotice>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer noticeType) {

        LambdaQueryWrapper<CampusNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CampusNotice::getStatus, 1); // 仅已发布
        if (StrUtil.isNotBlank(keyword)) wrapper.like(CampusNotice::getTitle, keyword);
        if (noticeType != null) wrapper.eq(CampusNotice::getNoticeType, noticeType);
        wrapper.orderByDesc(CampusNotice::getPublishTime);

        Page<CampusNotice> page = noticeService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /** 公告详情 + 自动标记已读 */
    @GetMapping("/{id}")
    public Result<CampusNotice> detail(@PathVariable Long id) {
        CampusNotice notice = noticeService.getById(id);
        if (notice == null) throw new BusinessException("公告不存在");

        // 登录用户自动标记已读
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            long readCount = noticeReadService.count(
                    new LambdaQueryWrapper<CampusNoticeRead>()
                            .eq(CampusNoticeRead::getNoticeId, id)
                            .eq(CampusNoticeRead::getUserId, userId)
            );
            if (readCount == 0) {
                CampusNoticeRead read = new CampusNoticeRead();
                read.setNoticeId(id);
                read.setUserId(userId);
                noticeReadService.save(read);
            }
        } catch (Exception ignored) {}

        return Result.success(notice);
    }

    /** 新增公告（草稿） */
    @PostMapping
    @SaCheckPermission("svc:notice:add")
    @LogRecord(module = "公告管理", type = "新增")
    public Result<Void> add(@RequestBody CampusNotice notice) {
        notice.setPublishUserId(StpUtil.getLoginIdAsLong());
        notice.setStatus(0); // 草稿
        noticeService.save(notice);
        return Result.success();
    }

    /** 发布公告 */
    @PutMapping("/{id}/publish")
    @SaCheckPermission("svc:notice:edit")
    @LogRecord(module = "公告管理", type = "发布")
    public Result<Void> publish(@PathVariable Long id) {
        CampusNotice notice = noticeService.getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        noticeService.updateById(notice);
        return Result.success();
    }

    /** 更新公告 */
    @PutMapping
    @SaCheckPermission("svc:notice:edit")
    public Result<Void> update(@RequestBody CampusNotice notice) {
        noticeService.updateById(notice);
        return Result.success();
    }

    /** 删除公告 */
    @DeleteMapping("/{id}")
    @SaCheckPermission("svc:notice:delete")
    @LogRecord(module = "公告管理", type = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success();
    }
}

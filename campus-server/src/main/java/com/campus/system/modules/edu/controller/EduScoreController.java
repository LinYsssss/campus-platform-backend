package com.campus.system.modules.edu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.edu.entity.EduScore;
import com.campus.system.modules.edu.entity.EduScoreAppeal;
import com.campus.system.modules.edu.service.IEduScoreAppealService;
import com.campus.system.modules.edu.service.IEduScoreService;
import com.campus.system.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 成绩管理控制器。
 */
@RestController
@RequestMapping("/edu/score")
@RequiredArgsConstructor
@Tag(name = "成绩管理", description = "成绩录入、审核与申诉处理接口")
public class EduScoreController {

    private final IEduScoreService scoreService;
    private final IEduScoreAppealService appealService;

    @GetMapping("/page")
    @SaCheckPermission("edu:score:list")
    @Operation(summary = "分页查询成绩")
    public Result<PageResult<EduScore>> page(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "审核状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<EduScore> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(EduScore::getCourseId, courseId);
        }
        if (SecurityUtils.hasRole("student")) {
            wrapper.eq(EduScore::getStudentId, SecurityUtils.getCurrentUserId());
        } else if (studentId != null) {
            wrapper.eq(EduScore::getStudentId, studentId);
        }
        if (StrUtil.isNotBlank(semester)) {
            wrapper.eq(EduScore::getSemester, semester);
        }
        if (status != null) {
            wrapper.eq(EduScore::getStatus, status);
        }
        wrapper.orderByDesc(EduScore::getId);

        Page<EduScore> page = scoreService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    @PostMapping
    @SaCheckPermission("edu:score:add")
    @LogRecord(module = "成绩管理", type = "录入")
    @Operation(summary = "录入成绩")
    public Result<Void> add(@RequestBody EduScore score) {
        validateScoreRange(score);
        score.setTeacherId(StpUtil.getLoginIdAsLong());
        score.setStatus(0);
        scoreService.save(score);
        return Result.success();
    }

    @PostMapping("/import")
    @SaCheckPermission("edu:score:add")
    @LogRecord(module = "成绩管理", type = "批量导入")
    @Operation(summary = "批量导入成绩", description = "上传 Excel 模板按课程和学期批量录入成绩，表头：学号 / 成绩 / 成绩类型 / 等级")
    public Result<String> importScores(
            @Parameter(description = "课程ID") @RequestParam Long courseId,
            @Parameter(description = "学期，如 2025-2026-1") @RequestParam String semester,
            @Parameter(description = "Excel 文件") @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Excel 文件不能为空");
        }
        return Result.success(scoreService.importScores(file, courseId, semester));
    }

    @PutMapping
    @SaCheckPermission("edu:score:edit")
    @LogRecord(module = "成绩管理", type = "修改")
    @Operation(summary = "更新成绩")
    public Result<Void> update(@RequestBody EduScore score) {
        EduScore existing = scoreService.getById(score.getId());
        if (existing == null) {
            throw new BusinessException("成绩记录不存在");
        }
        if (existing.getStatus() == 2) {
            throw new BusinessException("该成绩已归档审核通过，禁止修改");
        }

        validateScoreRange(score);
        score.setStatus(0);
        scoreService.updateById(score);
        return Result.success();
    }

    @PutMapping("/{id}/audit")
    @SaCheckRole("admin")
    @LogRecord(module = "成绩管理", type = "审核")
    @Operation(summary = "审核成绩")
    public Result<Void> audit(
            @Parameter(description = "成绩ID") @PathVariable Long id,
            @Parameter(description = "审核状态，1-驳回，2-归档") @RequestParam Integer status,
            @Parameter(description = "审核备注") @RequestParam(required = false) String remark) {

        if (status != 1 && status != 2) {
            throw new BusinessException("审核状态只能为 1(驳回) 或 2(归档)");
        }

        EduScore score = scoreService.getById(id);
        if (score == null) {
            throw new BusinessException("成绩记录不存在");
        }

        score.setStatus(status);
        score.setAuditUserId(StpUtil.getLoginIdAsLong());
        score.setAuditTime(LocalDateTime.now());
        score.setAuditRemark(remark);
        scoreService.updateById(score);
        return Result.success();
    }

    @PostMapping("/appeal")
    @Operation(summary = "提交成绩申诉")
    public Result<Void> submitAppeal(@RequestBody EduScoreAppeal appeal) {
        Long studentId = StpUtil.getLoginIdAsLong();
        appeal.setStudentId(studentId);
        appeal.setStatus(0);
        if (StrUtil.isBlank(appeal.getAttachmentPath())) {
            throw new BusinessException("申诉必须上传佐证图片凭证");
        }

        long count = appealService.count(
                new LambdaQueryWrapper<EduScoreAppeal>()
                        .eq(EduScoreAppeal::getScoreId, appeal.getScoreId())
                        .eq(EduScoreAppeal::getStudentId, studentId)
                        .ne(EduScoreAppeal::getStatus, 2)
        );
        if (count > 0) {
            throw new BusinessException("该成绩已有待处理的申诉工单");
        }

        appealService.save(appeal);
        return Result.success();
    }

    @GetMapping("/appeal/page")
    @SaCheckPermission("edu:score:list")
    @Operation(summary = "分页查询成绩申诉")
    public Result<PageResult<EduScoreAppeal>> appealPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "处理状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<EduScoreAppeal> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(EduScoreAppeal::getStatus, status);
        }
        wrapper.orderByDesc(EduScoreAppeal::getId);

        Page<EduScoreAppeal> page = appealService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    @PutMapping("/appeal/{id}/handle")
    @SaCheckPermission("edu:score:edit")
    @LogRecord(module = "成绩管理", type = "处理申诉")
    @Operation(summary = "处理成绩申诉")
    public Result<Void> handleAppeal(
            @Parameter(description = "申诉ID") @PathVariable Long id,
            @Parameter(description = "处理状态，1-受理，2-驳回") @RequestParam Integer status,
            @Parameter(description = "处理结果") @RequestParam(required = false) String result) {

        if (status != 1 && status != 2) {
            throw new BusinessException("处理状态只能为 1(受理) 或 2(驳回)");
        }

        EduScoreAppeal appeal = appealService.getById(id);
        if (appeal == null) {
            throw new BusinessException("申诉记录不存在");
        }
        if (appeal.getStatus() != 0) {
            throw new BusinessException("该申诉已处理");
        }

        appeal.setStatus(status);
        appeal.setHandlerId(StpUtil.getLoginIdAsLong());
        appeal.setHandleTime(LocalDateTime.now());
        appeal.setHandleResult(result);
        appealService.updateById(appeal);

        if (status == 1) {
            EduScore score = scoreService.getById(appeal.getScoreId());
            if (score != null && score.getStatus() == 2) {
                score.setStatus(1);
                score.setAuditRemark("因申诉受理，成绩已解锁");
                scoreService.updateById(score);
            }
        }
        return Result.success();
    }

    private void validateScoreRange(EduScore score) {
        if (score.getScore() != null && score.getScoreType() != null && score.getScoreType() == 0) {
            double value = score.getScore().doubleValue();
            if (value < 0 || value > 100) {
                throw new BusinessException("百分制成绩必须在0到100分之间，当前值: " + score.getScore());
            }
        }
    }
}

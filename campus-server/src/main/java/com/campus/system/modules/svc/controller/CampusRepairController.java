package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusRepairOrder;
import com.campus.system.modules.svc.service.ICampusRepairOrderService;
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

import java.time.LocalDateTime;

/**
 * 报修工单控制器。
 */
@RestController
@RequestMapping("/svc/repair")
@RequiredArgsConstructor
@Tag(name = "报修管理", description = "校园报修提交与处理接口")
public class CampusRepairController {

    private final ICampusRepairOrderService repairService;

    @GetMapping("/page")
    @SaCheckPermission("svc:repair:list")
    @Operation(summary = "分页查询报修工单")
    public Result<PageResult<CampusRepairOrder>> page(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "工单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "紧急程度") @RequestParam(required = false) Integer urgencyLevel) {

        LambdaQueryWrapper<CampusRepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(CampusRepairOrder::getStatus, status);
        }
        if (urgencyLevel != null) {
            wrapper.eq(CampusRepairOrder::getUrgencyLevel, urgencyLevel);
        }
        wrapper.orderByDesc(CampusRepairOrder::getUrgencyLevel).orderByDesc(CampusRepairOrder::getId);

        Page<CampusRepairOrder> page = repairService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    @GetMapping("/my")
    @Operation(summary = "查询我的报修工单")
    public Result<PageResult<CampusRepairOrder>> myOrders(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<CampusRepairOrder> page = repairService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<CampusRepairOrder>()
                        .eq(CampusRepairOrder::getApplicantId, userId)
                        .orderByDesc(CampusRepairOrder::getId)
        );
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    @PostMapping
    @Operation(summary = "提交报修工单")
    public Result<Void> submit(@RequestBody CampusRepairOrder order) {
        order.setApplicantId(SecurityUtils.getCurrentUserId());
        order.setOrderNo("RP" + IdUtil.getSnowflakeNextIdStr());
        order.setStatus(0);
        if (order.getUrgencyLevel() == null) {
            order.setUrgencyLevel(0);
        }

        if (order.getImagePaths() == null || order.getImagePaths().trim().isEmpty()) {
            throw new BusinessException("请上传损坏区域照片作为报修凭证");
        }

        if (order.getRoomId() != null) {
            long pendingCount = repairService.count(
                    new LambdaQueryWrapper<CampusRepairOrder>()
                            .eq(CampusRepairOrder::getRoomId, order.getRoomId())
                            .eq(CampusRepairOrder::getApplicantId, order.getApplicantId())
                            .in(CampusRepairOrder::getStatus, 0, 1)
            );
            if (pendingCount > 0) {
                throw new BusinessException("该房间已有未完成的报修工单，请勿重复提交");
            }
        }

        repairService.save(order);
        return Result.success();
    }

    @PutMapping("/{id}/accept")
    @SaCheckPermission("svc:repair:handle")
    @LogRecord(module = "报修管理", type = "受理")
    @Operation(summary = "受理报修工单")
    public Result<Void> accept(
            @Parameter(description = "工单ID") @PathVariable Long id,
            @Parameter(description = "处理人ID") @RequestParam Long handlerId) {
        CampusRepairOrder order = repairService.getById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("工单状态不允许受理");
        }
        order.setStatus(1);
        order.setHandlerId(handlerId);
        order.setHandleTime(LocalDateTime.now());
        repairService.updateById(order);
        return Result.success();
    }

    @PutMapping("/{id}/finish")
    @SaCheckPermission("svc:repair:handle")
    @LogRecord(module = "报修管理", type = "完成")
    @Operation(summary = "完成报修工单")
    public Result<Void> finish(
            @Parameter(description = "工单ID") @PathVariable Long id,
            @Parameter(description = "完成备注") @RequestParam(required = false) String remark) {
        CampusRepairOrder order = repairService.getById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("工单状态不允许完成操作");
        }
        order.setStatus(2);
        order.setFinishTime(LocalDateTime.now());
        order.setFinishRemark(remark);
        repairService.updateById(order);
        return Result.success();
    }

    @PutMapping("/{id}/verify")
    @SaCheckRole("admin")
    @LogRecord(module = "报修管理", type = "验收")
    @Operation(summary = "验收报修工单", description = "仅管理员可对已完成工单进行竣工验收")
    public Result<Void> verify(
            @Parameter(description = "工单ID") @PathVariable Long id,
            @Parameter(description = "验收评分，1到5分") @RequestParam Integer score,
            @Parameter(description = "验收备注") @RequestParam(required = false) String remark) {
        CampusRepairOrder order = repairService.getById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("工单未完成，不可验收");
        }
        if (score < 1 || score > 5) {
            throw new BusinessException("满意度评分需在1到5之间");
        }

        order.setStatus(3);
        order.setVerifyUserId(SecurityUtils.getCurrentUserId());
        order.setVerifyTime(LocalDateTime.now());
        order.setVerifyScore(score);
        order.setVerifyRemark(remark);
        repairService.updateById(order);
        return Result.success();
    }
}

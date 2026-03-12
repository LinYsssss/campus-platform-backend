package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusRepairOrder;
import com.campus.system.modules.svc.service.ICampusRepairOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 报修工单控制器
 * 生命周期：提交(0) → 受理处理中(1) → 已完成(2) → 已验收(3)
 */
@RestController
@RequestMapping("/svc/repair")
@RequiredArgsConstructor
public class CampusRepairController {

    private final ICampusRepairOrderService repairService;

    /** 分页查询工单 */
    @GetMapping("/page")
    @SaCheckPermission("svc:repair:list")
    public Result<PageResult<CampusRepairOrder>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer urgencyLevel) {

        LambdaQueryWrapper<CampusRepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(CampusRepairOrder::getStatus, status);
        if (urgencyLevel != null) wrapper.eq(CampusRepairOrder::getUrgencyLevel, urgencyLevel);
        wrapper.orderByDesc(CampusRepairOrder::getUrgencyLevel).orderByDesc(CampusRepairOrder::getId);

        Page<CampusRepairOrder> page = repairService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /** 我的报修列表 */
    @GetMapping("/my")
    public Result<PageResult<CampusRepairOrder>> myOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<CampusRepairOrder> page = repairService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<CampusRepairOrder>()
                        .eq(CampusRepairOrder::getApplicantId, userId)
                        .orderByDesc(CampusRepairOrder::getId)
        );
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /** 提交报修 */
    @PostMapping
    public Result<Void> submit(@RequestBody CampusRepairOrder order) {
        order.setApplicantId(StpUtil.getLoginIdAsLong());
        order.setOrderNo("RP" + IdUtil.getSnowflakeNextIdStr());
        order.setStatus(0); // 待处理
        if (order.getUrgencyLevel() == null) order.setUrgencyLevel(0);
        repairService.save(order);
        return Result.success();
    }

    /** 受理工单（分配处理人） */
    @PutMapping("/{id}/accept")
    @SaCheckPermission("svc:repair:handle")
    @LogRecord(module = "报修管理", type = "受理")
    public Result<Void> accept(@PathVariable Long id, @RequestParam Long handlerId) {
        CampusRepairOrder order = repairService.getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 0) throw new BusinessException("工单状态不允许受理");
        order.setStatus(1);
        order.setHandlerId(handlerId);
        order.setHandleTime(LocalDateTime.now());
        repairService.updateById(order);
        return Result.success();
    }

    /** 完成维修 */
    @PutMapping("/{id}/finish")
    @SaCheckPermission("svc:repair:handle")
    @LogRecord(module = "报修管理", type = "完成")
    public Result<Void> finish(@PathVariable Long id, @RequestParam(required = false) String remark) {
        CampusRepairOrder order = repairService.getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 1) throw new BusinessException("工单状态不允许完成操作");
        order.setStatus(2);
        order.setFinishTime(LocalDateTime.now());
        order.setFinishRemark(remark);
        repairService.updateById(order);
        return Result.success();
    }

    /** 报修人验收评分 */
    @PutMapping("/{id}/verify")
    public Result<Void> verify(@PathVariable Long id,
                               @RequestParam Integer score,
                               @RequestParam(required = false) String remark) {
        CampusRepairOrder order = repairService.getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 2) throw new BusinessException("工单未完成，不可验收");
        if (score < 1 || score > 5) throw new BusinessException("满意度评分需在1-5之间");

        order.setStatus(3);
        order.setVerifyUserId(StpUtil.getLoginIdAsLong());
        order.setVerifyTime(LocalDateTime.now());
        order.setVerifyScore(score);
        order.setVerifyRemark(remark);
        repairService.updateById(order);
        return Result.success();
    }
}

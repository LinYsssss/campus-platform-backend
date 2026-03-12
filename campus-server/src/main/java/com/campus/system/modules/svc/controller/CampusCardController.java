package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusCardLoss;
import com.campus.system.modules.svc.entity.CampusCardRecord;
import com.campus.system.modules.svc.service.ICampusCardLossService;
import com.campus.system.modules.svc.service.ICampusCardRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 校园卡管理控制器（消费/充值记录 + 挂失/解挂）
 */
@RestController
@RequestMapping("/svc/card")
@RequiredArgsConstructor
public class CampusCardController {

    private final ICampusCardRecordService cardRecordService;
    private final ICampusCardLossService cardLossService;

    /** 查询我的消费/充值记录 */
    @GetMapping("/record/my")
    public Result<PageResult<CampusCardRecord>> myRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer transactionType) {
        Long studentId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<CampusCardRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CampusCardRecord::getStudentId, studentId);
        if (transactionType != null) wrapper.eq(CampusCardRecord::getTransactionType, transactionType);
        wrapper.orderByDesc(CampusCardRecord::getTransactionTime);

        Page<CampusCardRecord> page = cardRecordService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /** 管理员分页查询全部交易记录 */
    @GetMapping("/record/page")
    @SaCheckPermission("svc:card:list")
    public Result<PageResult<CampusCardRecord>> recordPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Long studentId) {
        LambdaQueryWrapper<CampusCardRecord> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(CampusCardRecord::getStudentId, studentId);
        wrapper.orderByDesc(CampusCardRecord::getTransactionTime);
        Page<CampusCardRecord> page = cardRecordService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /** 充值 */
    @PostMapping("/recharge")
    @SaCheckPermission("svc:card:recharge")
    @LogRecord(module = "校园卡", type = "充值")
    public Result<Void> recharge(@RequestParam Long studentId, @RequestParam String cardNo,
                                  @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("充值金额必须大于0");

        // 查最新余额
        CampusCardRecord last = cardRecordService.getOne(
                new LambdaQueryWrapper<CampusCardRecord>()
                        .eq(CampusCardRecord::getStudentId, studentId)
                        .orderByDesc(CampusCardRecord::getTransactionTime)
                        .last("LIMIT 1")
        );
        BigDecimal balance = (last != null && last.getBalance() != null) ? last.getBalance() : BigDecimal.ZERO;

        CampusCardRecord record = new CampusCardRecord();
        record.setStudentId(studentId);
        record.setCardNo(cardNo);
        record.setTransactionType(1); // 充值
        record.setAmount(amount);
        record.setBalance(balance.add(amount));
        record.setLocation("管理员充值");
        record.setTransactionTime(LocalDateTime.now());
        cardRecordService.save(record);
        return Result.success();
    }

    /** 挂失校园卡 */
    @PostMapping("/loss/report")
    public Result<Void> reportLoss(@RequestParam String cardNo) {
        Long studentId = StpUtil.getLoginIdAsLong();
        CampusCardLoss loss = new CampusCardLoss();
        loss.setStudentId(studentId);
        loss.setCardNo(cardNo);
        loss.setStatus(0); // 已挂失
        cardLossService.save(loss);
        return Result.success();
    }

    /** 解挂校园卡 */
    @PutMapping("/loss/{id}/unlock")
    @SaCheckPermission("svc:card:edit")
    public Result<Void> unlockLoss(@PathVariable Long id) {
        CampusCardLoss loss = cardLossService.getById(id);
        if (loss == null) throw new BusinessException("挂失记录不存在");
        loss.setStatus(1); // 已解挂
        cardLossService.updateById(loss);
        return Result.success();
    }
}

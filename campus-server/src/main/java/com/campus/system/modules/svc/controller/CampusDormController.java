package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusDormitoryAllocation;
import com.campus.system.modules.svc.entity.CampusDormitoryBuilding;
import com.campus.system.modules.svc.entity.CampusDormitoryRoom;
import com.campus.system.modules.svc.service.ICampusDormitoryAllocationService;
import com.campus.system.modules.svc.service.ICampusDormitoryBuildingService;
import com.campus.system.modules.svc.service.ICampusDormitoryRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 宿舍管理控制器（楼栋 + 房间 + 分配）
 */
@RestController
@RequestMapping("/svc/dorm")
@RequiredArgsConstructor
public class CampusDormController {

    private final ICampusDormitoryBuildingService buildingService;
    private final ICampusDormitoryRoomService roomService;
    private final ICampusDormitoryAllocationService allocationService;

    @GetMapping("/building/list")
    @SaCheckPermission("svc:dorm:list")
    public Result<List<CampusDormitoryBuilding>> buildingList() {
        return Result.success(buildingService.list());
    }

    @PostMapping("/building")
    @SaCheckPermission("svc:dorm:add")
    @LogRecord(module = "宿舍管理", type = "新增楼栋")
    public Result<Void> addBuilding(@RequestBody CampusDormitoryBuilding building) {
        buildingService.save(building);
        return Result.success();
    }

    @PutMapping("/building")
    @SaCheckPermission("svc:dorm:edit")
    public Result<Void> updateBuilding(@RequestBody CampusDormitoryBuilding building) {
        buildingService.updateById(building);
        return Result.success();
    }

    @DeleteMapping("/building/{id}")
    @SaCheckPermission("svc:dorm:delete")
    public Result<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.removeById(id);
        return Result.success();
    }

    @GetMapping("/room/list")
    @SaCheckPermission("svc:dorm:list")
    public Result<List<CampusDormitoryRoom>> roomList(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<CampusDormitoryRoom> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) wrapper.eq(CampusDormitoryRoom::getBuildingId, buildingId);
        if (status != null) wrapper.eq(CampusDormitoryRoom::getStatus, status);
        wrapper.orderByAsc(CampusDormitoryRoom::getRoomCode);
        return Result.success(roomService.list(wrapper));
    }

    @PostMapping("/room")
    @SaCheckPermission("svc:dorm:add")
    public Result<Void> addRoom(@RequestBody CampusDormitoryRoom room) {
        room.setUsedCount(0);
        room.setStatus(0);
        roomService.save(room);
        return Result.success();
    }

    @PutMapping("/room")
    @SaCheckPermission("svc:dorm:edit")
    public Result<Void> updateRoom(@RequestBody CampusDormitoryRoom room) {
        roomService.updateById(room);
        return Result.success();
    }

    @GetMapping("/allocation/list")
    @SaCheckPermission("svc:dorm:list")
    public Result<List<CampusDormitoryAllocation>> allocationList(@RequestParam Long roomId) {
        return Result.success(allocationService.list(
                new LambdaQueryWrapper<CampusDormitoryAllocation>().eq(CampusDormitoryAllocation::getRoomId, roomId)
        ));
    }

    @PostMapping("/allocation")
    @SaCheckPermission("svc:dorm:edit")
    @LogRecord(module = "宿舍管理", type = "入住分配")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> allocate(@RequestBody CampusDormitoryAllocation allocation) {
        CampusDormitoryRoom room = roomService.getById(allocation.getRoomId());
        if (room == null) throw new BusinessException("房间不存在");
        if (room.getUsedCount() >= room.getBedCount()) throw new BusinessException("该房间已满员");
        if (Integer.valueOf(2).equals(room.getStatus())) throw new BusinessException("该房间正在维修，暂不可分配");
        if (allocation.getBedNumber() == null || allocation.getBedNumber() < 1 || allocation.getBedNumber() > room.getBedCount()) {
            throw new BusinessException("床位号不合法");
        }

        long studentActiveAllocation = allocationService.count(new LambdaQueryWrapper<CampusDormitoryAllocation>()
                .eq(CampusDormitoryAllocation::getStudentId, allocation.getStudentId())
                .eq(CampusDormitoryAllocation::getStatus, 0));
        if (studentActiveAllocation > 0) {
            throw new BusinessException("该学生已有在住床位，不可重复分配");
        }

        long occupiedBedCount = allocationService.count(new LambdaQueryWrapper<CampusDormitoryAllocation>()
                .eq(CampusDormitoryAllocation::getRoomId, allocation.getRoomId())
                .eq(CampusDormitoryAllocation::getBedNumber, allocation.getBedNumber())
                .eq(CampusDormitoryAllocation::getStatus, 0));
        if (occupiedBedCount > 0) {
            throw new BusinessException("该床位已被占用");
        }

        allocation.setStatus(0);
        allocationService.save(allocation);

        room.setUsedCount(room.getUsedCount() + 1);
        if (room.getUsedCount().equals(room.getBedCount())) room.setStatus(1);
        roomService.updateById(room);
        return Result.success();
    }

    @DeleteMapping("/allocation/{id}")
    @SaCheckPermission("svc:dorm:edit")
    @LogRecord(module = "宿舍管理", type = "退宿")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deallocate(@PathVariable Long id) {
        CampusDormitoryAllocation allocation = allocationService.getById(id);
        if (allocation == null) throw new BusinessException("分配记录不存在");
        if (!Integer.valueOf(0).equals(allocation.getStatus())) throw new BusinessException("该入住记录已退宿");

        allocation.setStatus(1);
        allocation.setCheckOutDate(LocalDate.now());
        allocationService.updateById(allocation);

        CampusDormitoryRoom room = roomService.getById(allocation.getRoomId());
        if (room != null && room.getUsedCount() > 0) {
            room.setUsedCount(room.getUsedCount() - 1);
            if (!Integer.valueOf(2).equals(room.getStatus())) {
                room.setStatus(room.getUsedCount().equals(room.getBedCount()) ? 1 : 0);
            }
            roomService.updateById(room);
        }
        return Result.success();
    }
}
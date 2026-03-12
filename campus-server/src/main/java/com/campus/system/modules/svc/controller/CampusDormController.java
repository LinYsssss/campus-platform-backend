package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

    // ============ 楼栋 ============

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

    // ============ 房间 ============

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

    // ============ 入住分配 ============

    @GetMapping("/allocation/list")
    @SaCheckPermission("svc:dorm:list")
    public Result<List<CampusDormitoryAllocation>> allocationList(@RequestParam Long roomId) {
        return Result.success(allocationService.list(
                new LambdaQueryWrapper<CampusDormitoryAllocation>().eq(CampusDormitoryAllocation::getRoomId, roomId)
        ));
    }

    /** 分配入住（自动更新房间已用床位数） */
    @PostMapping("/allocation")
    @SaCheckPermission("svc:dorm:edit")
    @LogRecord(module = "宿舍管理", type = "入住分配")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> allocate(@RequestBody CampusDormitoryAllocation allocation) {
        CampusDormitoryRoom room = roomService.getById(allocation.getRoomId());
        if (room == null) throw new BusinessException("房间不存在");
        if (room.getUsedCount() >= room.getBedCount()) throw new BusinessException("该房间已满员");

        allocation.setStatus(0);
        allocationService.save(allocation);

        // 更新已入住数
        room.setUsedCount(room.getUsedCount() + 1);
        if (room.getUsedCount().equals(room.getBedCount())) room.setStatus(1); // 满员
        roomService.updateById(room);
        return Result.success();
    }

    /** 退宿（自动释放床位） */
    @DeleteMapping("/allocation/{id}")
    @SaCheckPermission("svc:dorm:edit")
    @LogRecord(module = "宿舍管理", type = "退宿")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deallocate(@PathVariable Long id) {
        CampusDormitoryAllocation allocation = allocationService.getById(id);
        if (allocation == null) throw new BusinessException("分配记录不存在");

        allocationService.removeById(id);

        CampusDormitoryRoom room = roomService.getById(allocation.getRoomId());
        if (room != null && room.getUsedCount() > 0) {
            room.setUsedCount(room.getUsedCount() - 1);
            room.setStatus(0); // 有空位
            roomService.updateById(room);
        }
        return Result.success();
    }
}

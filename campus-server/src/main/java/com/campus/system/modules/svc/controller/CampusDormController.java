package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.Result;
import com.campus.system.modules.svc.entity.CampusDormitoryAllocation;
import com.campus.system.modules.svc.entity.CampusDormitoryBuilding;
import com.campus.system.modules.svc.entity.CampusDormitoryRoom;
import com.campus.system.modules.svc.service.ICampusDormitoryAllocationService;
import com.campus.system.modules.svc.service.ICampusDormitoryBuildingService;
import com.campus.system.modules.svc.service.ICampusDormitoryRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 宿舍管理控制器。
 */
@RestController
@RequestMapping("/svc/dorm")
@RequiredArgsConstructor
@Tag(name = "宿舍管理", description = "宿舍楼、房间与分配管理接口")
public class CampusDormController {

    private final ICampusDormitoryBuildingService buildingService;
    private final ICampusDormitoryRoomService roomService;
    private final ICampusDormitoryAllocationService allocationService;

    @GetMapping("/building/list")
    @SaCheckPermission("svc:dorm:list")
    @Operation(summary = "查询宿舍楼列表")
    public Result<List<CampusDormitoryBuilding>> buildingList() {
        return Result.success(buildingService.list());
    }

    @PostMapping("/building")
    @SaCheckPermission("svc:dorm:add")
    @LogRecord(module = "宿舍管理", type = "新增楼栋")
    @Operation(summary = "新增宿舍楼")
    public Result<Void> addBuilding(@RequestBody CampusDormitoryBuilding building) {
        buildingService.save(building);
        return Result.success();
    }

    @PutMapping("/building")
    @SaCheckPermission("svc:dorm:edit")
    @Operation(summary = "更新宿舍楼")
    public Result<Void> updateBuilding(@RequestBody CampusDormitoryBuilding building) {
        buildingService.updateById(building);
        return Result.success();
    }

    @DeleteMapping("/building/{id}")
    @SaCheckPermission("svc:dorm:delete")
    @Operation(summary = "删除宿舍楼")
    public Result<Void> deleteBuilding(@Parameter(description = "宿舍楼ID") @PathVariable Long id) {
        buildingService.removeById(id);
        return Result.success();
    }

    @GetMapping("/room/list")
    @SaCheckPermission("svc:dorm:list")
    @Operation(summary = "查询房间列表")
    public Result<List<CampusDormitoryRoom>> roomList(
            @Parameter(description = "宿舍楼ID") @RequestParam(required = false) Long buildingId,
            @Parameter(description = "房间状态") @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<CampusDormitoryRoom> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(CampusDormitoryRoom::getBuildingId, buildingId);
        }
        if (status != null) {
            wrapper.eq(CampusDormitoryRoom::getStatus, status);
        }
        wrapper.orderByAsc(CampusDormitoryRoom::getRoomCode);
        return Result.success(roomService.list(wrapper));
    }

    @PostMapping("/room")
    @SaCheckPermission("svc:dorm:add")
    @Operation(summary = "新增房间")
    public Result<Void> addRoom(@RequestBody CampusDormitoryRoom room) {
        room.setUsedCount(0);
        room.setStatus(0);
        roomService.save(room);
        return Result.success();
    }

    @PutMapping("/room")
    @SaCheckPermission("svc:dorm:edit")
    @Operation(summary = "更新房间")
    public Result<Void> updateRoom(@RequestBody CampusDormitoryRoom room) {
        roomService.updateById(room);
        return Result.success();
    }

    @GetMapping("/allocation/list")
    @SaCheckPermission("svc:dorm:list")
    @Operation(summary = "查询宿舍分配记录")
    public Result<List<CampusDormitoryAllocation>> allocationList(@Parameter(description = "房间ID") @RequestParam Long roomId) {
        return Result.success(allocationService.list(
                new LambdaQueryWrapper<CampusDormitoryAllocation>().eq(CampusDormitoryAllocation::getRoomId, roomId)
        ));
    }

    @PostMapping("/allocation")
    @SaCheckPermission("svc:dorm:edit")
    @LogRecord(module = "宿舍管理", type = "入住分配")
    @Operation(summary = "分配宿舍床位")
    public Result<Void> allocate(@RequestBody CampusDormitoryAllocation allocation) {
        allocationService.allocate(allocation);
        return Result.success();
    }

    @DeleteMapping("/allocation/{id}")
    @SaCheckPermission("svc:dorm:edit")
    @LogRecord(module = "宿舍管理", type = "退宿")
    @Operation(summary = "办理退宿")
    public Result<Void> deallocate(@Parameter(description = "分配记录ID") @PathVariable Long id) {
        allocationService.deallocate(id);
        return Result.success();
    }
}

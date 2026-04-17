package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusDormitoryAllocation;
import com.campus.system.modules.svc.entity.CampusDormitoryRoom;
import com.campus.system.modules.svc.mapper.CampusDormitoryAllocationMapper;
import com.campus.system.modules.svc.service.ICampusDormitoryAllocationService;
import com.campus.system.modules.svc.service.ICampusDormitoryRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CampusDormitoryAllocationServiceImpl extends ServiceImpl<CampusDormitoryAllocationMapper, CampusDormitoryAllocation> implements ICampusDormitoryAllocationService {

    private final ICampusDormitoryRoomService roomService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allocate(CampusDormitoryAllocation allocation) {
        CampusDormitoryRoom room = roomService.getById(allocation.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        if (room.getUsedCount() >= room.getBedCount()) {
            throw new BusinessException("该房间已满员");
        }
        if (Integer.valueOf(2).equals(room.getStatus())) {
            throw new BusinessException("该房间正在维修，暂不可分配");
        }
        if (allocation.getBedNumber() == null || allocation.getBedNumber() < 1 || allocation.getBedNumber() > room.getBedCount()) {
            throw new BusinessException("床位号不合法");
        }

        long studentActiveAllocation = this.count(new LambdaQueryWrapper<CampusDormitoryAllocation>()
                .eq(CampusDormitoryAllocation::getStudentId, allocation.getStudentId())
                .eq(CampusDormitoryAllocation::getStatus, 0));
        if (studentActiveAllocation > 0) {
            throw new BusinessException("该学生已有在住床位，不可重复分配");
        }

        long occupiedBedCount = this.count(new LambdaQueryWrapper<CampusDormitoryAllocation>()
                .eq(CampusDormitoryAllocation::getRoomId, allocation.getRoomId())
                .eq(CampusDormitoryAllocation::getBedNumber, allocation.getBedNumber())
                .eq(CampusDormitoryAllocation::getStatus, 0));
        if (occupiedBedCount > 0) {
            throw new BusinessException("该床位已被占用");
        }

        allocation.setStatus(0);
        this.save(allocation);

        room.setUsedCount(room.getUsedCount() + 1);
        if (room.getUsedCount().equals(room.getBedCount())) {
            room.setStatus(1);
        }
        roomService.updateById(room);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deallocate(Long allocationId) {
        CampusDormitoryAllocation allocation = this.getById(allocationId);
        if (allocation == null) {
            throw new BusinessException("分配记录不存在");
        }
        if (!Integer.valueOf(0).equals(allocation.getStatus())) {
            throw new BusinessException("该入住记录已退宿");
        }

        allocation.setStatus(1);
        allocation.setCheckOutDate(LocalDate.now());
        this.updateById(allocation);

        CampusDormitoryRoom room = roomService.getById(allocation.getRoomId());
        if (room != null && room.getUsedCount() > 0) {
            room.setUsedCount(room.getUsedCount() - 1);
            if (!Integer.valueOf(2).equals(room.getStatus())) {
                room.setStatus(room.getUsedCount().equals(room.getBedCount()) ? 1 : 0);
            }
            roomService.updateById(room);
        }
    }
}

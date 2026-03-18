package com.campus.system.modules.svc.controller;

import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusDormitoryAllocation;
import com.campus.system.modules.svc.entity.CampusDormitoryRoom;
import com.campus.system.modules.svc.service.ICampusDormitoryAllocationService;
import com.campus.system.modules.svc.service.ICampusDormitoryBuildingService;
import com.campus.system.modules.svc.service.ICampusDormitoryRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampusDormControllerTest {

    @Mock
    private ICampusDormitoryBuildingService buildingService;
    @Mock
    private ICampusDormitoryRoomService roomService;
    @Mock
    private ICampusDormitoryAllocationService allocationService;
    @InjectMocks
    private CampusDormController controller;

    @Test
    void allocateRejectsOccupiedBedNumbers() {
        CampusDormitoryRoom room = new CampusDormitoryRoom();
        room.setId(1L);
        room.setBedCount(4);
        room.setUsedCount(1);
        room.setStatus(0);
        when(roomService.getById(1L)).thenReturn(room);
        when(allocationService.count(any())).thenReturn(1L);

        CampusDormitoryAllocation allocation = new CampusDormitoryAllocation();
        allocation.setRoomId(1L);
        allocation.setStudentId(300L);
        allocation.setBedNumber(1);

        assertThrows(BusinessException.class, () -> controller.allocate(allocation));
        verify(allocationService, never()).save(any());
    }

    @Test
    void deallocateMarksCheckoutInsteadOfDeletingAllocation() {
        CampusDormitoryAllocation allocation = new CampusDormitoryAllocation();
        allocation.setId(5L);
        allocation.setRoomId(1L);
        allocation.setStudentId(200L);
        allocation.setStatus(0);

        CampusDormitoryRoom room = new CampusDormitoryRoom();
        room.setId(1L);
        room.setBedCount(4);
        room.setUsedCount(2);
        room.setStatus(1);

        when(allocationService.getById(5L)).thenReturn(allocation);
        when(roomService.getById(1L)).thenReturn(room);

        controller.deallocate(5L);

        ArgumentCaptor<CampusDormitoryAllocation> captor = ArgumentCaptor.forClass(CampusDormitoryAllocation.class);
        verify(allocationService).updateById(captor.capture());
        verify(allocationService, never()).removeById(5L);
        assertEquals(1, captor.getValue().getStatus());
        assertEquals(LocalDate.now(), captor.getValue().getCheckOutDate());
        assertEquals(1, room.getUsedCount());
        assertEquals(0, room.getStatus());
        verify(roomService).updateById(room);
    }
}
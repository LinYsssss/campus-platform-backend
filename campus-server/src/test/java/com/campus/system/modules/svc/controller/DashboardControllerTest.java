package com.campus.system.modules.svc.controller;

import com.alicp.jetcache.Cache;
import com.campus.system.common.api.Result;
import com.campus.system.modules.edu.service.IEduAttendanceSessionService;
import com.campus.system.modules.edu.service.IEduCourseService;
import com.campus.system.modules.svc.entity.CampusDashboardSnapshot;
import com.campus.system.modules.svc.service.ICampusBookBorrowService;
import com.campus.system.modules.svc.service.ICampusBookService;
import com.campus.system.modules.svc.service.ICampusDashboardSnapshotService;
import com.campus.system.modules.svc.service.ICampusRepairOrderService;
import com.campus.system.modules.sys.service.ISysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private ISysUserService userService;
    @Mock
    private IEduCourseService courseService;
    @Mock
    private IEduAttendanceSessionService attendanceSessionService;
    @Mock
    private ICampusRepairOrderService repairService;
    @Mock
    private ICampusBookService bookService;
    @Mock
    private ICampusBookBorrowService borrowService;
    @Mock
    private ICampusDashboardSnapshotService snapshotService;
    @Mock
    private Cache<String, CampusDashboardSnapshot> overviewSnapshotCache;
    @InjectMocks
    private DashboardController controller;

    @BeforeEach
    void setUp() throws Exception {
        Field field = DashboardController.class.getDeclaredField("overviewSnapshotCache");
        field.setAccessible(true);
        field.set(controller, overviewSnapshotCache);
    }

    @Test
    void overviewReturnsCachedSnapshotWhenAvailable() {
        CampusDashboardSnapshot cached = new CampusDashboardSnapshot();
        cached.setSnapshotData("{\"totalUsers\":99,\"pendingRepairOrders\":2}");
        when(overviewSnapshotCache.get("dashboard_overview")).thenReturn(cached);

        Result<Map<String, Object>> result = controller.overview();

        assertEquals(99, ((Number) result.getData().get("totalUsers")).intValue());
        assertEquals(2, ((Number) result.getData().get("pendingRepairOrders")).intValue());
        verifyNoInteractions(userService, courseService, attendanceSessionService, repairService, bookService, borrowService, snapshotService);
    }

    @Test
    void saveSnapshotUpdatesExistingSnapshotInsteadOfInsertingDuplicateKey() {
        when(userService.count()).thenReturn(10L);
        when(courseService.count()).thenReturn(5L);
        when(attendanceSessionService.count()).thenReturn(3L);
        when(repairService.count()).thenReturn(7L);
        when(repairService.count(any())).thenReturn(2L);
        when(bookService.count()).thenReturn(11L);
        when(borrowService.count(any())).thenReturn(4L);

        CampusDashboardSnapshot existing = new CampusDashboardSnapshot();
        existing.setId(9L);
        when(snapshotService.getOne(any(), eq(false))).thenReturn(existing);

        controller.saveSnapshot();

        ArgumentCaptor<CampusDashboardSnapshot> captor = ArgumentCaptor.forClass(CampusDashboardSnapshot.class);
        verify(snapshotService).updateById(captor.capture());
        verify(snapshotService, never()).save(any());
        verify(overviewSnapshotCache).put(eq("dashboard_overview"), any(CampusDashboardSnapshot.class));
        assertEquals(9L, captor.getValue().getId());
        assertEquals("dashboard_overview", captor.getValue().getSnapshotKey());
        assertNotNull(captor.getValue().getSnapshotTime());
    }
}
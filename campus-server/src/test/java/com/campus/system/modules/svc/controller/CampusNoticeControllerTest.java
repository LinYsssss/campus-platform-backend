package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.modules.svc.entity.CampusNotice;
import com.campus.system.modules.svc.service.ICampusNoticeReadService;
import com.campus.system.modules.svc.service.ICampusNoticeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampusNoticeControllerTest {

    @Mock
    private ICampusNoticeService noticeService;
    @Mock
    private ICampusNoticeReadService noticeReadService;
    @InjectMocks
    private CampusNoticeController controller;

    @Test
    void pageFiltersOutNoticesOutsideCurrentUserRole() {
        CampusNotice publicNotice = new CampusNotice();
        publicNotice.setId(1L);
        publicNotice.setStatus(1);
        publicNotice.setNoticeType(0);
        publicNotice.setTitle("public");
        publicNotice.setPublishTime(LocalDateTime.now());

        CampusNotice teacherOnly = new CampusNotice();
        teacherOnly.setId(2L);
        teacherOnly.setStatus(1);
        teacherOnly.setNoticeType(1);
        teacherOnly.setTargetRole("teacher");
        teacherOnly.setTitle("teacher only");
        teacherOnly.setPublishTime(LocalDateTime.now());

        Page<CampusNotice> page = new Page<>(1, 10);
        page.setTotal(2);
        page.setRecords(List.of(publicNotice, teacherOnly));
        when(noticeService.page(any(Page.class), any())).thenReturn(page);

        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {
            stpUtil.when(() -> StpUtil.hasRole("admin")).thenReturn(false);
            stpUtil.when(() -> StpUtil.hasRole("teacher")).thenReturn(false);
            stpUtil.when(() -> StpUtil.hasRole("student")).thenReturn(true);

            Result<PageResult<CampusNotice>> result = controller.page(1, 10,  null);

            assertEquals(1, result.getData().getList().size());
            assertEquals(1L, result.getData().getList().get(0).getId());
        }
    }
}

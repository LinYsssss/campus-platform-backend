package com.campus.system.modules.svc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.svc.entity.CampusCardLoss;
import com.campus.system.modules.svc.service.ICampusCardLossService;
import com.campus.system.modules.svc.service.ICampusCardRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CampusCardControllerTest {

    @Mock
    private ICampusCardRecordService cardRecordService;
    @Mock
    private ICampusCardLossService cardLossService;
    @InjectMocks
    private CampusCardController controller;

    @Test
    void reportLossSetsLossTimeBeforeSave() {
        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(100L);

            controller.reportLoss("CARD-100");

            ArgumentCaptor<CampusCardLoss> captor = ArgumentCaptor.forClass(CampusCardLoss.class);
            verify(cardLossService).save(captor.capture());
            assertEquals(100L, captor.getValue().getStudentId());
            assertEquals("CARD-100", captor.getValue().getCardNo());
            assertEquals(0, captor.getValue().getStatus());
            assertNotNull(captor.getValue().getLossTime());
        }
    }

    @Test
    void rechargeRejectsManualLocalRechargeRequests() {
        assertThrows(BusinessException.class, () -> controller.recharge(100L, "CARD-100", BigDecimal.TEN));
        verify(cardRecordService, never()).save(Mockito.any());
    }
}
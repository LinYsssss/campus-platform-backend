package com.campus.system.modules.svc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.system.modules.svc.entity.CampusDormitoryAllocation;

public interface ICampusDormitoryAllocationService extends IService<CampusDormitoryAllocation> {

    /**
     * 分配床位：校验房间状态/满员/床位合法与否/学生是否已有在住，保存分配记录并同步更新房间入住数。
     */
    void allocate(CampusDormitoryAllocation allocation);

    /**
     * 办理退宿：更新分配状态与退宿日期，同步回扣房间入住数。
     */
    void deallocate(Long allocationId);
}

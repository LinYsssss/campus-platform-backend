package com.campus.system.modules.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.edu.entity.EduLeaveRequest;
import com.campus.system.modules.edu.mapper.EduLeaveRequestMapper;
import com.campus.system.modules.edu.service.IEduLeaveRequestService;
import org.springframework.stereotype.Service;

@Service
public class EduLeaveRequestServiceImpl extends ServiceImpl<EduLeaveRequestMapper, EduLeaveRequest> implements IEduLeaveRequestService {
}

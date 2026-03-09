package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusBook;
import com.campus.system.modules.svc.mapper.CampusBookMapper;
import com.campus.system.modules.svc.service.ICampusBookService;
import org.springframework.stereotype.Service;

@Service
public class CampusBookServiceImpl extends ServiceImpl<CampusBookMapper, CampusBook> implements ICampusBookService {
}

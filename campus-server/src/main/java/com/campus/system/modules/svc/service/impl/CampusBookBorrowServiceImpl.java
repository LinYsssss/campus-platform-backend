package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusBookBorrow;
import com.campus.system.modules.svc.mapper.CampusBookBorrowMapper;
import com.campus.system.modules.svc.service.ICampusBookBorrowService;
import org.springframework.stereotype.Service;

@Service
public class CampusBookBorrowServiceImpl extends ServiceImpl<CampusBookBorrowMapper, CampusBookBorrow> implements ICampusBookBorrowService {
}

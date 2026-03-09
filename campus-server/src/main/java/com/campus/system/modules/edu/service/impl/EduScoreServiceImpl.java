package com.campus.system.modules.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.edu.entity.EduScore;
import com.campus.system.modules.edu.mapper.EduScoreMapper;
import com.campus.system.modules.edu.service.IEduScoreService;
import org.springframework.stereotype.Service;

@Service
public class EduScoreServiceImpl extends ServiceImpl<EduScoreMapper, EduScore> implements IEduScoreService {
}

package com.campus.system.modules.sys.controller;

import com.campus.system.modules.sys.service.ISysDictTypeService;
import com.campus.system.modules.sys.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典管理控制器（合并字典类型 + 字典数据）
 */
@RestController
@RequestMapping("/sys/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final ISysDictTypeService sysDictTypeService;
    private final ISysDictDataService sysDictDataService;
}

package com.campus.system.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.sys.entity.SysDictData;
import com.campus.system.modules.sys.entity.SysDictType;
import com.campus.system.modules.sys.service.ISysDictDataService;
import com.campus.system.modules.sys.service.ISysDictTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器（字典类型 + 字典数据统一入口）
 */
@RestController
@RequestMapping("/sys/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final ISysDictTypeService dictTypeService;
    private final ISysDictDataService dictDataService;

    // ============ 字典类型 ============

    /** 查询全部字典类型列表 */
    @GetMapping("/type/list")
    @SaCheckPermission("sys:dict:list")
    public Result<List<SysDictType>> typeList() {
        return Result.success(dictTypeService.list(
                new LambdaQueryWrapper<SysDictType>().orderByAsc(SysDictType::getId)
        ));
    }

    /** 新增字典类型 */
    @PostMapping("/type")
    @SaCheckPermission("sys:dict:add")
    public Result<Void> addType(@Valid @RequestBody SysDictType dictType) {
        long count = dictTypeService.count(
                new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dictType.getDictType())
        );
        if (count > 0) throw new BusinessException("字典类型标识 '" + dictType.getDictType() + "' 已存在");
        dictTypeService.save(dictType);
        return Result.success();
    }

    /** 更新字典类型 */
    @PutMapping("/type")
    @SaCheckPermission("sys:dict:edit")
    public Result<Void> updateType(@Valid @RequestBody SysDictType dictType) {
        dictTypeService.updateById(dictType);
        return Result.success();
    }

    /** 删除字典类型（同步清理该类型下所有字典数据） */
    @DeleteMapping("/type/{id}")
    @SaCheckPermission("sys:dict:delete")
    public Result<Void> deleteType(@PathVariable Long id) {
        SysDictType type = dictTypeService.getById(id);
        if (type == null) throw new BusinessException("字典类型不存在");
        dictTypeService.removeById(id);
        dictDataService.remove(
                new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, type.getDictType())
        );
        return Result.success();
    }

    // ============ 字典数据 ============

    /** 根据字典类型标识查询字典数据列表 */
    @GetMapping("/data/{dictType}")
    public Result<List<SysDictData>> dataByType(@PathVariable String dictType) {
        return Result.success(dictDataService.list(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, 0)
                        .orderByAsc(SysDictData::getSortOrder)
        ));
    }

    /** 新增字典数据 */
    @PostMapping("/data")
    @SaCheckPermission("sys:dict:add")
    public Result<Void> addData(@Valid @RequestBody SysDictData dictData) {
        dictDataService.save(dictData);
        return Result.success();
    }

    /** 更新字典数据 */
    @PutMapping("/data")
    @SaCheckPermission("sys:dict:edit")
    public Result<Void> updateData(@Valid @RequestBody SysDictData dictData) {
        dictDataService.updateById(dictData);
        return Result.success();
    }

    /** 删除字典数据 */
    @DeleteMapping("/data/{id}")
    @SaCheckPermission("sys:dict:delete")
    public Result<Void> deleteData(@PathVariable Long id) {
        dictDataService.removeById(id);
        return Result.success();
    }
}

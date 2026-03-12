package com.campus.system.modules.edu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.modules.edu.entity.EduTimetable;
import com.campus.system.modules.edu.service.IEduTimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课表排课控制器
 */
@RestController
@RequestMapping("/edu/timetable")
@RequiredArgsConstructor
public class EduTimetableController {

    private final IEduTimetableService timetableService;

    /**
     * 分页查询课表
     */
    @GetMapping("/page")
    @SaCheckPermission("edu:timetable:list")
    public Result<PageResult<EduTimetable>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) Long teacherId) {

        LambdaQueryWrapper<EduTimetable> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(semester)) wrapper.eq(EduTimetable::getSemester, semester);
        if (StrUtil.isNotBlank(className)) wrapper.eq(EduTimetable::getClassName, className);
        if (teacherId != null) wrapper.eq(EduTimetable::getTeacherId, teacherId);
        wrapper.orderByAsc(EduTimetable::getDayOfWeek).orderByAsc(EduTimetable::getStartSection);

        Page<EduTimetable> page = timetableService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /**
     * 查询"我的课表"（当前登录教师）
     */
    @GetMapping("/my")
    public Result<List<EduTimetable>> myTimetable(@RequestParam String semester) {
        Long teacherId = StpUtil.getLoginIdAsLong();
        return Result.success(timetableService.list(
                new LambdaQueryWrapper<EduTimetable>()
                        .eq(EduTimetable::getTeacherId, teacherId)
                        .eq(EduTimetable::getSemester, semester)
                        .orderByAsc(EduTimetable::getDayOfWeek)
                        .orderByAsc(EduTimetable::getStartSection)
        ));
    }

    /**
     * 按班级查询课表
     */
    @GetMapping("/class")
    public Result<List<EduTimetable>> classTimetable(
            @RequestParam String className, @RequestParam String semester) {
        return Result.success(timetableService.list(
                new LambdaQueryWrapper<EduTimetable>()
                        .eq(EduTimetable::getClassName, className)
                        .eq(EduTimetable::getSemester, semester)
                        .orderByAsc(EduTimetable::getDayOfWeek)
                        .orderByAsc(EduTimetable::getStartSection)
        ));
    }

    /**
     * 新增排课
     */
    @PostMapping
    @SaCheckPermission("edu:timetable:add")
    public Result<Void> add(@Valid @RequestBody EduTimetable timetable) {
        timetableService.save(timetable);
        return Result.success();
    }

    /**
     * 更新排课
     */
    @PutMapping
    @SaCheckPermission("edu:timetable:edit")
    public Result<Void> update(@Valid @RequestBody EduTimetable timetable) {
        timetableService.updateById(timetable);
        return Result.success();
    }

    /**
     * 删除排课
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("edu:timetable:delete")
    public Result<Void> delete(@PathVariable Long id) {
        timetableService.removeById(id);
        return Result.success();
    }
}

package com.campus.system.modules.edu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.edu.entity.EduCourse;
import com.campus.system.modules.edu.entity.EduCourseClass;
import com.campus.system.modules.edu.entity.EduCourseTeacher;
import com.campus.system.modules.edu.service.IEduCourseService;
import com.campus.system.modules.edu.mapper.EduCourseClassMapper;
import com.campus.system.modules.edu.mapper.EduCourseTeacherMapper;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/edu/course")
@RequiredArgsConstructor
public class EduCourseController {

    private final IEduCourseService courseService;
    private final EduCourseTeacherMapper courseTeacherMapper;
    private final EduCourseClassMapper courseClassMapper;

    /**
     * 分页查询课程列表
     */
    @GetMapping("/page")
    @SaCheckPermission("edu:course:list")
    public Result<PageResult<EduCourse>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<EduCourse> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(EduCourse::getCourseName, keyword).or().like(EduCourse::getCourseCode, keyword));
        }
        if (StrUtil.isNotBlank(semester)) wrapper.eq(EduCourse::getSemester, semester);
        if (status != null) wrapper.eq(EduCourse::getStatus, status);
        wrapper.orderByDesc(EduCourse::getId);

        Page<EduCourse> page = courseService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /**
     * 课程详情（含授课教师ID + 关联班级）
     */
    @GetMapping("/{id}")
    @SaCheckPermission("edu:course:query")
    public Result<CourseDetailVO> detail(@PathVariable Long id) {
        EduCourse course = courseService.getById(id);
        if (course == null) throw new BusinessException("课程不存在");

        List<Long> teacherIds = courseTeacherMapper.selectList(
                new LambdaQueryWrapper<EduCourseTeacher>().eq(EduCourseTeacher::getCourseId, id)
        ).stream().map(EduCourseTeacher::getTeacherId).collect(Collectors.toList());

        List<String> classNames = courseClassMapper.selectList(
                new LambdaQueryWrapper<EduCourseClass>().eq(EduCourseClass::getCourseId, id)
        ).stream().map(EduCourseClass::getClassName).collect(Collectors.toList());

        CourseDetailVO vo = new CourseDetailVO();
        vo.setCourse(course);
        vo.setTeacherIds(teacherIds);
        vo.setClassNames(classNames);
        return Result.success(vo);
    }

    /**
     * 新增课程（含教师和班级绑定）
     */
    @PostMapping
    @SaCheckPermission("edu:course:add")
    @LogRecord(module = "课程管理", type = "新增")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> create(@Valid @RequestBody CourseCreateDTO dto) {
        // 课程编码唯一
        long count = courseService.count(new LambdaQueryWrapper<EduCourse>().eq(EduCourse::getCourseCode, dto.getCourseCode()));
        if (count > 0) throw new BusinessException("课程编码 '" + dto.getCourseCode() + "' 已存在");

        EduCourse course = new EduCourse();
        BeanUtil.copyProperties(dto, course, "teacherIds", "classNames");
        course.setStatus(0);
        courseService.save(course);

        bindTeachers(course.getId(), dto.getTeacherIds());
        bindClasses(course.getId(), dto.getClassNames());
        return Result.success();
    }

    /**
     * 更新课程
     */
    @PutMapping
    @SaCheckPermission("edu:course:edit")
    @LogRecord(module = "课程管理", type = "修改")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(@Valid @RequestBody CourseCreateDTO dto) {
        if (dto.getId() == null) throw new BusinessException("课程ID不能为空");
        EduCourse course = courseService.getById(dto.getId());
        if (course == null) throw new BusinessException("课程不存在");

        BeanUtil.copyProperties(dto, course, "teacherIds", "classNames");
        courseService.updateById(course);

        // 重新绑定
        courseTeacherMapper.delete(new LambdaQueryWrapper<EduCourseTeacher>().eq(EduCourseTeacher::getCourseId, dto.getId()));
        courseClassMapper.delete(new LambdaQueryWrapper<EduCourseClass>().eq(EduCourseClass::getCourseId, dto.getId()));
        bindTeachers(dto.getId(), dto.getTeacherIds());
        bindClasses(dto.getId(), dto.getClassNames());
        return Result.success();
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("edu:course:delete")
    @LogRecord(module = "课程管理", type = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return Result.success();
    }

    /**
     * 结课操作
     */
    @PutMapping("/{id}/finish")
    @SaCheckPermission("edu:course:edit")
    @LogRecord(module = "课程管理", type = "结课")
    public Result<Void> finish(@PathVariable Long id) {
        EduCourse course = courseService.getById(id);
        if (course == null) throw new BusinessException("课程不存在");
        course.setStatus(1);
        courseService.updateById(course);
        return Result.success();
    }

    // ============ 私有方法 ============

    private void bindTeachers(Long courseId, List<Long> teacherIds) {
        if (teacherIds == null) return;
        teacherIds.forEach(tid -> {
            EduCourseTeacher ct = new EduCourseTeacher();
            ct.setCourseId(courseId);
            ct.setTeacherId(tid);
            courseTeacherMapper.insert(ct);
        });
    }

    private void bindClasses(Long courseId, List<String> classNames) {
        if (classNames == null) return;
        classNames.forEach(cn -> {
            EduCourseClass cc = new EduCourseClass();
            cc.setCourseId(courseId);
            cc.setClassName(cn);
            courseClassMapper.insert(cc);
        });
    }

    // ============ 内部 DTO/VO ============

    @Data
    public static class CourseCreateDTO {
        private Long id;
        private String courseName;
        private String courseCode;
        private java.math.BigDecimal credit;
        private Integer hours;
        private String semester;
        private String description;
        private List<Long> teacherIds;
        private List<String> classNames;
    }

    @Data
    public static class CourseDetailVO {
        private EduCourse course;
        private List<Long> teacherIds;
        private List<String> classNames;
    }
}

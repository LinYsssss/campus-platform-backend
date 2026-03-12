package com.campus.system.modules.edu.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.edu.entity.EduCourseEvaluation;
import com.campus.system.modules.edu.service.IEduCourseEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 课程评价控制器
 */
@RestController
@RequestMapping("/edu/evaluation")
@RequiredArgsConstructor
public class EduEvaluationController {

    private final IEduCourseEvaluationService evaluationService;

    /**
     * 分页查询某课程的评价列表
     */
    @GetMapping("/page")
    public Result<PageResult<EduCourseEvaluation>> page(
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<EduCourseEvaluation> page = evaluationService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<EduCourseEvaluation>()
                        .eq(EduCourseEvaluation::getCourseId, courseId)
                        .orderByDesc(EduCourseEvaluation::getId)
        );
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    /**
     * 学生提交课程评价（每人每课程只能评一次）
     */
    @PostMapping
    public Result<Void> submit(@Valid @RequestBody EduCourseEvaluation evaluation) {
        Long studentId = StpUtil.getLoginIdAsLong();
        evaluation.setStudentId(studentId);

        // 防重复评价
        long count = evaluationService.count(
                new LambdaQueryWrapper<EduCourseEvaluation>()
                        .eq(EduCourseEvaluation::getCourseId, evaluation.getCourseId())
                        .eq(EduCourseEvaluation::getStudentId, studentId)
        );
        if (count > 0) throw new BusinessException("您已对该课程提交过评价，不可重复提交");

        if (evaluation.getStarRating() == null || evaluation.getStarRating() < 1 || evaluation.getStarRating() > 5) {
            throw new BusinessException("星级评分需在1-5之间");
        }
        evaluationService.save(evaluation);
        return Result.success();
    }

    /**
     * 删除评价（管理员权limited）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        evaluationService.removeById(id);
        return Result.success();
    }
}

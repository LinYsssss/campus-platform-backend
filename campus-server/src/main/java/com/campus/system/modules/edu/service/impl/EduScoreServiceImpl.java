package com.campus.system.modules.edu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.edu.entity.EduScore;
import com.campus.system.modules.edu.mapper.EduScoreMapper;
import com.campus.system.modules.edu.service.IEduScoreService;
import com.campus.system.modules.sys.entity.SysUser;
import com.campus.system.modules.sys.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EduScoreServiceImpl extends ServiceImpl<EduScoreMapper, EduScore> implements IEduScoreService {

    private final ISysUserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importScores(MultipartFile file, Long courseId, String semester) {
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (StrUtil.isBlank(semester)) {
            throw new BusinessException("学期不能为空");
        }

        Long teacherId = StpUtil.getLoginIdAsLong();
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String, Object>> rows = reader.readAll();
            if (rows.isEmpty()) {
                throw new BusinessException("Excel 数据为空");
            }

            int success = 0, fail = 0;
            StringBuilder errMsg = new StringBuilder();

            for (int i = 0; i < rows.size(); i++) {
                Map<String, Object> row = rows.get(i);
                int rowNum = i + 2;
                try {
                    String studentNo = StrUtil.toStringOrNull(row.get("学号"));
                    if (StrUtil.isBlank(studentNo)) {
                        fail++;
                        errMsg.append("第").append(rowNum).append("行：学号为空; ");
                        continue;
                    }

                    SysUser student = userService.getOne(
                            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, studentNo)
                    );
                    if (student == null) {
                        fail++;
                        errMsg.append("第").append(rowNum).append("行：学号'").append(studentNo).append("'不存在; ");
                        continue;
                    }

                    long duplicate = this.count(
                            new LambdaQueryWrapper<EduScore>()
                                    .eq(EduScore::getCourseId, courseId)
                                    .eq(EduScore::getStudentId, student.getId())
                                    .eq(EduScore::getSemester, semester)
                    );
                    if (duplicate > 0) {
                        fail++;
                        errMsg.append("第").append(rowNum).append("行：学号'").append(studentNo)
                                .append("'在当前学期已有成绩记录; ");
                        continue;
                    }

                    String typeStr = StrUtil.toStringOrNull(row.get("成绩类型"));
                    Integer scoreType = parseScoreType(typeStr);

                    EduScore score = new EduScore();
                    score.setCourseId(courseId);
                    score.setStudentId(student.getId());
                    score.setTeacherId(teacherId);
                    score.setSemester(semester);
                    score.setScoreType(scoreType);
                    score.setStatus(0);

                    if (scoreType == 0) {
                        Object scoreVal = row.get("成绩");
                        if (scoreVal == null || StrUtil.isBlank(scoreVal.toString())) {
                            fail++;
                            errMsg.append("第").append(rowNum).append("行：百分制成绩为空; ");
                            continue;
                        }
                        BigDecimal value;
                        try {
                            value = new BigDecimal(scoreVal.toString().trim());
                        } catch (NumberFormatException ex) {
                            fail++;
                            errMsg.append("第").append(rowNum).append("行：成绩'").append(scoreVal)
                                    .append("'不是合法数字; ");
                            continue;
                        }
                        if (value.doubleValue() < 0 || value.doubleValue() > 100) {
                            fail++;
                            errMsg.append("第").append(rowNum).append("行：成绩必须在0到100之间，当前值 ")
                                    .append(value).append("; ");
                            continue;
                        }
                        score.setScore(value);
                    } else {
                        String level = StrUtil.toStringOrNull(row.get("等级"));
                        if (StrUtil.isBlank(level)) {
                            fail++;
                            errMsg.append("第").append(rowNum).append("行：等级制成绩的等级列为空; ");
                            continue;
                        }
                        score.setScoreLevel(level.trim().toUpperCase());
                    }

                    this.save(score);
                    success++;
                } catch (Exception e) {
                    fail++;
                    errMsg.append("第").append(rowNum).append("行：").append(e.getMessage()).append("; ");
                }
            }

            String result = "导入完成，成功 " + success + " 条，失败 " + fail + " 条。";
            if (fail > 0) result += " 失败详情：" + errMsg;
            return result;
        } catch (IOException e) {
            throw new BusinessException("Excel 文件读取失败：" + e.getMessage());
        }
    }

    private Integer parseScoreType(String typeStr) {
        if (StrUtil.isBlank(typeStr)) return 0;
        String t = typeStr.trim();
        if ("等级制".equals(t) || "等级".equals(t) || "1".equals(t)) return 1;
        return 0;
    }
}

package com.campus.system.modules.sys.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.sys.dto.SysUserCreateDTO;
import com.campus.system.modules.sys.dto.SysUserQueryDTO;
import com.campus.system.modules.sys.dto.SysUserUpdateDTO;
import com.campus.system.modules.sys.entity.SysRole;
import com.campus.system.modules.sys.entity.SysUser;
import com.campus.system.modules.sys.entity.SysUserRole;
import com.campus.system.modules.sys.mapper.SysRoleMapper;
import com.campus.system.modules.sys.mapper.SysUserMapper;
import com.campus.system.modules.sys.mapper.SysUserRoleMapper;
import com.campus.system.modules.sys.service.ISysUserService;
import com.campus.system.modules.sys.vo.SysUserVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理业务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;

    @Override
    public PageResult<SysUserVO> queryUserPage(SysUserQueryDTO query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 关键字模糊查询（匹配 username 或 realName）
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, query.getKeyword())
                    .or()
                    .like(SysUser::getRealName, query.getKeyword())
            );
        }
        if (query.getUserType() != null) {
            wrapper.eq(SysUser::getUserType, query.getUserType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        if (StrUtil.isNotBlank(query.getDeptName())) {
            wrapper.like(SysUser::getDeptName, query.getDeptName());
        }
        wrapper.orderByDesc(SysUser::getId);

        Page<SysUser> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        List<SysUserVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), voList, (long) query.getPageNum(), (long) query.getPageSize());
    }

    @Override
    public SysUserVO getUserDetail(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(SysUserCreateDTO dto) {
        // 校验用户名唯一
        long count = this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) throw new BusinessException("用户名 '" + dto.getUsername() + "' 已存在");

        SysUser user = new SysUser();
        BeanUtil.copyProperties(dto, user, "password", "roleIds");
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setStatus(0);
        user.setLoginFailCount(0);
        this.save(user);

        // 绑定角色
        bindRoles(user.getId(), dto.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUserUpdateDTO dto) {
        SysUser existing = this.getById(dto.getId());
        if (existing == null) throw new BusinessException("用户不存在");

        BeanUtil.copyProperties(dto, existing, "id", "roleIds");
        this.updateById(existing);

        // 重新绑定角色（先删后插）
        if (dto.getRoleIds() != null) {
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, dto.getId()));
            bindRoles(dto.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if ("admin".equals(user.getUsername())) throw new BusinessException("不允许删除超级管理员账号");
        this.removeById(userId);
        // 同步清理角色绑定
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
    }

    @Override
    public void toggleStatus(Long userId, Integer status) {
        SysUser user = this.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if ("admin".equals(user.getUsername())) throw new BusinessException("不允许停用超级管理员账号");
        user.setStatus(status);
        if (status == 0) {
            user.setLoginFailCount(0);
            user.setLockTime(null);
        }
        this.updateById(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setLoginFailCount(0);
        user.setLockTime(null);
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importUsers(MultipartFile file) {
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<Map<String, Object>> rows = reader.readAll();
            int success = 0, fail = 0;
            StringBuilder errMsg = new StringBuilder();

            for (int i = 0; i < rows.size(); i++) {
                Map<String, Object> row = rows.get(i);
                try {
                    String username = StrUtil.toStringOrNull(row.get("用户名"));
                    String realName = StrUtil.toStringOrNull(row.get("姓名"));
                    String password = StrUtil.toStringOrNull(row.get("密码"));

                    if (StrUtil.isBlank(username) || StrUtil.isBlank(realName)) {
                        fail++;
                        errMsg.append("第").append(i + 2).append("行：用户名或姓名为空; ");
                        continue;
                    }
                    // 查重
                    long count = this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
                    if (count > 0) {
                        fail++;
                        errMsg.append("第").append(i + 2).append("行：用户名'").append(username).append("'已存在; ");
                        continue;
                    }

                    SysUser user = new SysUser();
                    user.setUsername(username);
                    user.setRealName(realName);
                    user.setPassword(BCrypt.hashpw(StrUtil.isBlank(password) ? "123456" : password));
                    user.setGender(parseGender(StrUtil.toStringOrNull(row.get("性别"))));
                    user.setPhone(StrUtil.toStringOrNull(row.get("手机号")));
                    user.setEmail(StrUtil.toStringOrNull(row.get("邮箱")));
                    user.setDeptName(StrUtil.toStringOrNull(row.get("院系")));
                    user.setClassName(StrUtil.toStringOrNull(row.get("班级")));
                    user.setUserType(parseUserType(StrUtil.toStringOrNull(row.get("用户类型"))));
                    user.setStatus(0);
                    user.setLoginFailCount(0);
                    this.save(user);
                    success++;
                } catch (Exception e) {
                    fail++;
                    errMsg.append("第").append(i + 2).append("行：").append(e.getMessage()).append("; ");
                }
            }
            String result = "导入完成，成功 " + success + " 条，失败 " + fail + " 条。";
            if (fail > 0) result += " 失败详情：" + errMsg;
            return result;
        } catch (IOException e) {
            throw new BusinessException("Excel 文件读取失败：" + e.getMessage());
        }
    }

    @Override
    public void exportUsers(SysUserQueryDTO query, HttpServletResponse response) {
        // 查询所有匹配数据（不分页）
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(SysUser::getUsername, query.getKeyword()).or().like(SysUser::getRealName, query.getKeyword()));
        }
        if (query.getUserType() != null) wrapper.eq(SysUser::getUserType, query.getUserType());
        if (query.getStatus() != null) wrapper.eq(SysUser::getStatus, query.getStatus());
        List<SysUser> list = this.list(wrapper);

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用户数据.xlsx", StandardCharsets.UTF_8));

            ExcelWriter writer = ExcelUtil.getWriter(true);
            writer.addHeaderAlias("username", "用户名");
            writer.addHeaderAlias("realName", "姓名");
            writer.addHeaderAlias("gender", "性别");
            writer.addHeaderAlias("phone", "手机号");
            writer.addHeaderAlias("email", "邮箱");
            writer.addHeaderAlias("deptName", "院系");
            writer.addHeaderAlias("className", "班级");
            writer.addHeaderAlias("userType", "用户类型");
            writer.addHeaderAlias("status", "状态");

            List<Map<String, Object>> exportData = list.stream().map(u -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("username", u.getUsername());
                map.put("realName", u.getRealName());
                map.put("gender", u.getGender() == null ? "未知" : u.getGender() == 1 ? "男" : u.getGender() == 2 ? "女" : "未知");
                map.put("phone", u.getPhone());
                map.put("email", u.getEmail());
                map.put("deptName", u.getDeptName());
                map.put("className", u.getClassName());
                map.put("userType", u.getUserType() == null ? "" : u.getUserType() == 0 ? "学生" : u.getUserType() == 1 ? "教师" : "管理员");
                map.put("status", u.getStatus() == null ? "" : u.getStatus() == 0 ? "正常" : u.getStatus() == 1 ? "停用" : "锁定");
                return map;
            }).collect(Collectors.toList());

            writer.write(exportData, true);
            writer.flush(response.getOutputStream(), true);
            writer.close();
        } catch (IOException e) {
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }

    // ============ 私有辅助方法 ============

    private SysUserVO toVO(SysUser user) {
        SysUserVO vo = new SysUserVO();
        BeanUtil.copyProperties(user, vo);
        // 查询用户角色名
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId())
        );
        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        } else {
            vo.setRoleNames(Collections.emptyList());
        }
        return vo;
    }

    private void bindRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return;
        roleIds.forEach(roleId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        });
    }

    private Integer parseGender(String text) {
        if ("男".equals(text)) return 1;
        if ("女".equals(text)) return 2;
        return 0;
    }

    private Integer parseUserType(String text) {
        if ("教师".equals(text)) return 1;
        if ("管理员".equals(text)) return 2;
        return 0; // 默认学生
    }
}

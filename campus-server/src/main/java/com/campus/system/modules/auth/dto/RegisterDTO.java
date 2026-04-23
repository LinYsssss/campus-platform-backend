package com.campus.system.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求 DTO。
 */
@Data
@Schema(name = "注册请求", description = "开放注册时提交的参数")
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @Schema(description = "用户名（学号/工号/账号）")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20位之间")
    @Schema(description = "登录密码")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @NotNull(message = "用户类型不能为空")
    @Schema(description = "用户类型，0-学生，1-教师")
    private Integer userType;

    @Schema(description = "院系或部门")
    private String deptName;

    @Schema(description = "班级名称（学生专属）")
    private String className;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String captchaCode;

    @NotBlank(message = "验证码标识不能为空")
    @Schema(description = "验证码标识")
    private String captchaKey;
}

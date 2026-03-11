package com.campus.system.modules.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 验证码返回视图对象
 */
@Data
@AllArgsConstructor
public class CaptchaVO {

    /** 唯一标识Key（前端回传时用于校验） */
    private String captchaKey;

    /** Base64编码的验证码图片 */
    private String captchaImage;
}

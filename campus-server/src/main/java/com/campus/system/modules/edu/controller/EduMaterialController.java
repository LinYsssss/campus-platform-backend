package com.campus.system.modules.edu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.edu.entity.EduCourseMaterial;
import com.campus.system.modules.edu.service.IEduCourseMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 教学课件资料控制器
 */
@RestController
@RequestMapping("/edu/material")
@RequiredArgsConstructor
public class EduMaterialController {

    private final IEduCourseMaterialService materialService;

    /** 文件上传根路径 */
    @Value("${campus.upload-path:./uploads}")
    private String uploadPath;

    /** 允许上传的文件类型白名单 */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "zip", "rar", "jpg", "png"
    );

    /** 单文件大小限制 10MB（需求规格要求） */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 查询课程下的资料列表
     */
    @GetMapping("/list")
    public Result<List<EduCourseMaterial>> list(@RequestParam Long courseId) {
        return Result.success(materialService.list(
                new LambdaQueryWrapper<EduCourseMaterial>()
                        .eq(EduCourseMaterial::getCourseId, courseId)
                        .orderByDesc(EduCourseMaterial::getId)
        ));
    }

    /**
     * 上传课件资料
     */
    @PostMapping("/upload")
    @SaCheckPermission("edu:material:upload")
    @LogRecord(module = "课件管理", type = "上传")
    public Result<EduCourseMaterial> upload(
            @RequestParam Long courseId,
            @RequestParam("file") MultipartFile file) throws IOException {

        // 1. 校验文件
        if (file.isEmpty()) throw new BusinessException("文件不能为空");
        if (file.getSize() > MAX_FILE_SIZE) throw new BusinessException("文件大小不能超过10MB");

        String originalName = file.getOriginalFilename();
        String ext = FileUtil.extName(originalName);
        if (!ALLOWED_EXTENSIONS.contains(ext != null ? ext.toLowerCase() : "")) {
            throw new BusinessException("不允许上传该类型文件，仅支持：" + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // 2. 计算 MD5 防重复
        String md5 = DigestUtil.md5Hex(file.getInputStream());
        long existCount = materialService.count(
                new LambdaQueryWrapper<EduCourseMaterial>()
                        .eq(EduCourseMaterial::getCourseId, courseId)
                        .eq(EduCourseMaterial::getFileMd5, md5)
        );
        if (existCount > 0) throw new BusinessException("该文件已存在（MD5重复），无需重复上传");

        // 3. 保存文件到本地磁盘
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        String dirPath = uploadPath + "/material/" + courseId;
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        File dest = new File(dir, storedName);
        file.transferTo(dest);

        // 4. 入库
        EduCourseMaterial material = new EduCourseMaterial();
        material.setCourseId(courseId);
        material.setUploadUserId(StpUtil.getLoginIdAsLong());
        material.setFileName(originalName);
        material.setFilePath(dirPath + "/" + storedName);
        material.setFileSize(file.getSize());
        material.setFileType(file.getContentType());
        material.setFileMd5(md5);
        material.setDownloadCount(0);
        materialService.save(material);

        return Result.success(material);
    }

    /**
     * 下载课件资料
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        EduCourseMaterial material = materialService.getById(id);
        if (material == null) throw new BusinessException("资料不存在");

        File file = new File(material.getFilePath());
        if (!file.exists()) throw new BusinessException("文件已丢失，请联系管理员");

        // 下载次数 +1
        materialService.update(new LambdaUpdateWrapper<EduCourseMaterial>()
                .eq(EduCourseMaterial::getId, id)
                .setSql("download_count = download_count + 1"));

        Resource resource = new FileSystemResource(file);
        String encodedName = URLEncoder.encode(material.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * 删除课件资料
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("edu:material:delete")
    @LogRecord(module = "课件管理", type = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        EduCourseMaterial material = materialService.getById(id);
        if (material != null) {
            // 删除物理文件
            FileUtil.del(material.getFilePath());
            materialService.removeById(id);
        }
        return Result.success();
    }
}

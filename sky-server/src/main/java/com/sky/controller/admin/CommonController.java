package com.sky.controller.admin;


import com.sky.annotation.AutoFill;
import com.sky.enumeration.OperationType;
import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(@RequestBody MultipartFile file) throws IOException {
        log.info("文件上传");
        byte[] bytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String uuidFilename = UUID.randomUUID().toString()+suffix;
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String url = aliOssUtil.upload(bytes, localDate+"/"+uuidFilename);



        return Result.success(url);
    }
}

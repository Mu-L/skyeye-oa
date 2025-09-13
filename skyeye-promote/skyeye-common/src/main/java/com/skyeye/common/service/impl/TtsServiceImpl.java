/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.service.TtsService;
import com.skyeye.common.util.FileUtil;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TtsServiceImpl
 * @Description: 文字转语音服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/01/01 00:00
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class TtsServiceImpl implements TtsService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    // 用于延迟删除文件的调度器
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void textToSpeech(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        try {
            // 获取参数
            String text = params.get("text").toString();
            String format = params.getOrDefault("format", "wav").toString();

            if (StrUtil.isEmpty(text)) {
                throw new CustomException("文本内容不能为空");
            }

            // 限制文本长度
            if (text.length() > 1000) {
                throw new CustomException("文本长度不能超过1000个字符");
            }

            log.info("开始文字转语音，文本长度: {}", text.length());

            // 生成语音文件
            String audioFilePath = generateSpeechFile(text, format);

            // 获取文件信息
            File audioFile = new File(audioFilePath);
            long fileSize = audioFile.length();
            String fileName = audioFile.getName();
            String visitPath = String.format("%s/tts/%s", FileConstants.FileUploadPath.getVisitPath(100), fileName);

            // 1分钟后自动删除临时文件
            scheduleDeleteTempFile(audioFilePath);

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("fileSize", fileSize);
            result.put("audioFilePath", visitPath);
            result.put("duration", estimateDuration(text));
            result.put("text", text);
            result.put("format", format);

            outputObject.setBean(result);
            outputObject.settotal(1);

            log.info("文字转语音完成，文件大小: {} bytes", fileSize);

        } catch (Exception e) {
            log.error("文字转语音失败", e);
            throw new CustomException("文字转语音失败: " + e.getMessage());
        }
    }

    /**
     * 生成语音文件
     */
    private String generateSpeechFile(String text, String format) throws Exception {

        // 创建输出目录
        String outputDir = tPath + FileConstants.FileUploadPath.getSavePath(100) + CommonCharConstants.SLASH_MARK + "tts";
        FileUtil.createDirs(outputDir);

        // 生成文件名
        String fileName = "tts_" + System.currentTimeMillis() + "." + format;
        String filePath = outputDir + CommonCharConstants.SLASH_MARK + fileName;

        // 尝试多种TTS方案
        boolean success = false;

        // 方案1：尝试使用系统TTS
        try {
            log.info("尝试使用系统TTS生成语音");
            success = generateWithSystemTTS(text, filePath);
        } catch (Exception e) {
            log.warn("系统TTS失败: {}", e.getMessage());
        }

        // 方案2：如果系统TTS失败，使用在线TTS服务
        if (!success) {
            try {
                log.info("尝试使用在线TTS服务");
                success = generateWithOnlineTTS(text, filePath);
            } catch (Exception e) {
                log.warn("在线TTS失败: {}", e.getMessage());
            }
        }

        return filePath;
    }

    /**
     * 使用系统TTS生成语音
     */
    private boolean generateWithSystemTTS(String text, String filePath) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("windows")) {
                return generateWithWindowsTTS(text, filePath);
            } else if (os.contains("mac")) {
                return generateWithMacTTS(text, filePath);
            } else if (os.contains("linux")) {
                return generateWithLinuxTTS(text, filePath);
            }
        } catch (Exception e) {
            log.warn("系统TTS执行失败: {}", e.getMessage());
            return false;
        }

        return false;
    }

    /**
     * Windows系统TTS
     */
    private boolean generateWithWindowsTTS(String text, String filePath) throws Exception {
        // 使用PowerShell的SpeechSynthesizer
        String command = String.format(
            "powershell -Command \"Add-Type -AssemblyName System.Speech; $synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; $synth.SetOutputToWaveFile('%s'); $synth.Speak('%s'); $synth.Dispose()\"",
            filePath.replace("\\", "\\\\"), text.replace("'", "''")
        );

        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode == 0 && new File(filePath).exists()) {
            log.info("Windows TTS 生成成功");
            return true;
        }

        return false;
    }

    /**
     * Mac系统TTS
     */
    private boolean generateWithMacTTS(String text, String filePath) throws Exception {
        String command = String.format("say -o '%s' '%s'", filePath, text.replace("'", "\\'"));

        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode == 0 && new File(filePath).exists()) {
            log.info("Mac TTS 生成成功");
            return true;
        }

        return false;
    }

    /**
     * Linux系统TTS
     */
    private boolean generateWithLinuxTTS(String text, String filePath) throws Exception {
        // 尝试使用espeak
        String command = String.format("espeak -s 150 -w '%s' '%s'", filePath, text.replace("'", "\\'"));

        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode == 0 && new File(filePath).exists()) {
            log.info("Linux TTS (espeak) 生成成功");
            return true;
        }

        return false;
    }

    /**
     * 使用在线TTS服务
     */
    private boolean generateWithOnlineTTS(String text, String filePath) throws Exception {
        try {
            // 使用Google Translate TTS (免费)
            return generateWithGoogleTTS(text, filePath);
        } catch (Exception e) {
            log.warn("Google TTS失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 使用Google Translate TTS
     */
    private boolean generateWithGoogleTTS(String text, String filePath) throws Exception {
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String url = String.format("https://translate.google.com/translate_tts?ie=UTF-8&tl=zh-cn&client=tw-ob&q=%s", encodedText);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        if (connection.getResponseCode() == 200) {
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(filePath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                log.info("Google TTS 生成成功");
                return true;
            }
        }

        return false;
    }


    /**
     * 将文件转换为Base64字符串
     */
    private String convertFileToBase64(String filePath) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            byte[] fileBytes = new byte[(int) new File(filePath).length()];
            fileInputStream.read(fileBytes);
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }

    /**
     * 安排1分钟后删除临时文件
     */
    private void scheduleDeleteTempFile(String filePath) {
        scheduler.schedule(() -> deleteTempFile(filePath), 60, TimeUnit.SECONDS);
        log.info("已安排1分钟后删除临时文件: {}", filePath);
    }

    /**
     * 删除临时文件
     */
    private void deleteTempFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("临时文件删除成功: {}", filePath);
                } else {
                    log.warn("临时文件删除失败: {}", filePath);
                }
            } else {
                log.info("临时文件不存在，可能已被删除: {}", filePath);
            }
        } catch (Exception e) {
            log.error("删除临时文件时发生错误: {}", e.getMessage());
        }
    }

    /**
     * 估算音频时长（秒）
     */
    private int estimateDuration(String text) {
        // 简单估算：平均每分钟150个字符
        return Math.max(1, (int) Math.ceil(text.length() / 2.5));
    }
}

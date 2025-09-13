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

        // 尝试使用系统TTS
        try {
            log.info("尝试使用系统TTS生成语音");
            generateWithSystemTTS(text, filePath);
        } catch (Exception e) {
            log.warn("系统TTS失败: {}", e.getMessage());
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
        try {
            // 检查espeak是否安装
            Process checkProcess = Runtime.getRuntime().exec("which espeak");
            int checkCode = checkProcess.waitFor();
            if (checkCode != 0) {
                log.warn("espeak未安装，请先安装: sudo apt-get install espeak");
                return false;
            }

            // 使用espeak生成语音，添加更多参数确保兼容性
            String command = String.format("espeak -s 150 -v zh -w '%s' '%s'", filePath, text.replace("'", "\\'"));
            log.info("执行Linux TTS命令: {}", command);

            Process process = Runtime.getRuntime().exec(command);
            
            // 读取错误输出，帮助调试
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getErrorStream()))) {
                String line;
                StringBuilder errorOutput = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
                if (errorOutput.length() > 0) {
                    log.warn("espeak错误输出: {}", errorOutput.toString());
                }
            }

            int exitCode = process.waitFor();
            log.info("espeak执行完成，退出码: {}", exitCode);

            // 检查文件是否生成
            File outputFile = new File(filePath);
            if (outputFile.exists() && outputFile.length() > 0) {
                log.info("Linux TTS (espeak) 生成成功，文件大小: {} bytes", outputFile.length());
                return true;
            } else {
                log.warn("Linux TTS (espeak) 文件未生成或为空，文件路径: {}", filePath);
                return false;
            }

        } catch (Exception e) {
            log.error("Linux TTS (espeak) 执行异常: {}", e.getMessage());
            return false;
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

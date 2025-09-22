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
 * @date: 2025/09/20 00:00
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class TtsServiceImpl implements TtsService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Value("${skyeye.text2audio.apiKey}")
    private String API_KEY;

    @Value("${skyeye.text2audio.secretKey}")
    private String SECRET_KEY;

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
     * Linux系统TTS - 百度智能云接口
     */
    private boolean generateWithLinuxTTS(String text, String filePath) {
        try {
            log.info("开始使用百度智能云TTS生成语音");
            // 获取access_token
            String accessToken = getBaiduAccessToken(API_KEY, SECRET_KEY);
            if (accessToken == null) {
                log.error("获取百度access_token失败");
                return false;
            }

            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("tex", text);
            params.put("tok", accessToken);
            params.put("cuid", "skyeye-tts");
            params.put("ctp", "1");
            params.put("lan", "zh");
            params.put("spd", "5");        // 语速
            params.put("pit", "5");        // 音调
            params.put("vol", "5");        // 音量
            params.put("per", "0");        // 发音人：0-女声，1-男声，3-度逍遥，4-度小娇
            params.put("aue", "6");        // 6为wav格式

            // 发送HTTP请求
            String url = "https://tsn.baidu.com/text2audio";
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36");

            // 构建请求体
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (postData.length() > 0) {
                    postData.append("&");
                }
                postData.append(entry.getKey()).append("=").append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            // 发送请求
            try (java.io.OutputStream os = connection.getOutputStream()) {
                os.write(postData.toString().getBytes("UTF-8"));
            }

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (java.io.InputStream inputStream = connection.getInputStream();
                     java.io.FileOutputStream outputStream = new java.io.FileOutputStream(filePath)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }

                File outputFile = new File(filePath);
                if (outputFile.exists() && outputFile.length() > 0) {
                    log.info("百度智能云TTS生成成功，文件大小: {} bytes", outputFile.length());
                    return true;
                }
            } else {
                log.error("百度智能云TTS请求失败，响应码: {}", responseCode);
            }

        } catch (Exception e) {
            log.error("百度智能云TTS执行异常: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 获取百度access_token
     */
    private String getBaiduAccessToken(String apiKey, String secretKey) {
        try {
            String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + secretKey;

            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // 简单的JSON解析
                    String responseBody = response.toString();
                    if (responseBody.contains("\"access_token\"")) {
                        int start = responseBody.indexOf("\"access_token\":\"") + 16;
                        int end = responseBody.indexOf("\"", start);
                        return responseBody.substring(start, end);
                    }
                }
            }

        } catch (Exception e) {
            log.error("获取百度access_token失败: {}", e.getMessage());
        }

        return null;
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

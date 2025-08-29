/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.enumeration.HttpMethodEnum;
import com.skyeye.common.util.HttpRequestUtil;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: GitCodeApiClient
 * @Description: GitCode API v5客户端工具类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Slf4j
@Component
public class GitCodeApiClient {

    private String baseUrl = "https://api.gitcode.com/api/v5";

    public String projectUrl = "https://gitcode.com/doc_wei/erp-pro";

    // Token配置
    private String accessToken;
    private String privateToken;
    private String authorizationToken;

    /**
     * 设置访问令牌
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 设置私有令牌
     */
    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    /**
     * 设置授权令牌
     */
    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    /**
     * 验证Token有效性
     * 根据GitCode官方API文档，支持三种认证方式
     * 
     * @return Token验证结果
     */
    public TokenValidationResult validateToken() {
        TokenValidationResult result = new TokenValidationResult();
        
        try {
            // 测试Authorization方式
            if (StrUtil.isNotEmpty(authorizationToken)) {
                if (validateAuthorizationToken(authorizationToken)) {
                    result.setValid(true);
                    result.setValidToken(authorizationToken);
                    result.setAuthType("Authorization");
                    log.info("Authorization Token验证成功");
                    return result;
                }
            }
            
            // 测试PRIVATE-TOKEN方式
            if (StrUtil.isNotEmpty(privateToken)) {
                if (validatePrivateToken(privateToken)) {
                    result.setValid(true);
                    result.setValidToken(privateToken);
                    result.setAuthType("PRIVATE-TOKEN");
                    log.info("PRIVATE-TOKEN验证成功");
                    return result;
                }
            }
            
            // 测试access_token方式
            if (StrUtil.isNotEmpty(accessToken)) {
                if (validateAccessToken(accessToken)) {
                    result.setValid(true);
                    result.setValidToken(accessToken);
                    result.setAuthType("access_token");
                    log.info("access_token验证成功");
                    return result;
                }
            }
            
            // 如果所有token都无效
            result.setValid(false);
            result.setErrorMessage("所有Token都无效");
            log.warn("所有Token验证失败");
            
        } catch (Exception e) {
            log.error("Token验证过程中发生异常", e);
            result.setValid(false);
            result.setErrorMessage("Token验证异常: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 验证Authorization Token
     * 使用 /api/v5/user 端点验证
     */
    private boolean validateAuthorizationToken(String token) {
        try {
            String endpoint = "/user";
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            headers.put("Authorization", "Bearer " + token);
            
            String responseBody = HttpRequestUtil.getDataByRequest(baseUrl + endpoint, 
                HttpMethodEnum.GET_REQUEST.getKey(), headers, null);
            
            JSONObject response = JSONUtil.parseObj(responseBody);
            
            // 检查响应状态，如果返回用户信息说明token有效
            return response != null && !response.isEmpty() && 
                   response.containsKey("id") && response.containsKey("login");
                   
        } catch (Exception e) {
            log.warn("Authorization Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证PRIVATE-TOKEN
     * 使用 /api/v5/user 端点验证
     */
    private boolean validatePrivateToken(String token) {
        try {
            String endpoint = "/user";
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            
            String responseBody = HttpRequestUtil.getDataByRequest(baseUrl + endpoint, 
                HttpMethodEnum.GET_REQUEST.getKey(), headers, null);
            
            JSONObject response = JSONUtil.parseObj(responseBody);
            
            // 检查响应状态，如果返回用户信息说明token有效
            return response != null && !response.isEmpty() && 
                   response.containsKey("id") && response.containsKey("login");
                   
        } catch (Exception e) {
            log.warn("PRIVATE-TOKEN验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证access_token
     * 使用 /api/v5/user 端点验证
     */
    private boolean validateAccessToken(String token) {
        try {
            String endpoint = "/user?access_token=" + token;
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            
            String responseBody = HttpRequestUtil.getDataByRequest(baseUrl + endpoint, 
                HttpMethodEnum.GET_REQUEST.getKey(), headers, null);
            
            JSONObject response = JSONUtil.parseObj(responseBody);
            
            // 检查响应状态，如果返回用户信息说明token有效
            return response != null && !response.isEmpty() && 
                   response.containsKey("id") && response.containsKey("login");
                   
        } catch (Exception e) {
            log.warn("access_token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前用户信息
     * 用于验证token并获取用户详情
     */
    public JSONObject getCurrentUser() {
        TokenValidationResult validation = validateToken();
        if (!validation.isValid()) {
            throw new CustomException("Token无效，无法获取用户信息: " + validation.getErrorMessage());
        }
        
        try {
            String endpoint = "/user";
            Map<String, String> headers = buildAuthHeaders(validation.getAuthType(), validation.getValidToken());
            
            String responseBody = HttpRequestUtil.getDataByRequest(baseUrl + endpoint, 
                HttpMethodEnum.GET_REQUEST.getKey(), headers, null);
            
            return JSONUtil.parseObj(responseBody);
            
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            throw new CustomException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 构建认证头
     */
    private Map<String, String> buildAuthHeaders(String authType, String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        
        switch (authType) {
            case "Authorization":
                headers.put("Authorization", "Bearer " + token);
                break;
            case "PRIVATE-TOKEN":
                headers.put("PRIVATE-TOKEN", token);
                break;
            case "access_token":
                // access_token通过query参数传递，不需要在header中设置
                break;
            default:
                log.warn("未知的认证类型: {}", authType);
        }
        
        return headers;
    }

    public JSONObject get(String endpoint) {
        return sendRequest(HttpMethodEnum.GET_REQUEST.getKey(), endpoint, null);
    }

    public JSONObject post(String endpoint, Map<String, Object> data) {
        return sendRequest(HttpMethodEnum.POST_REQUEST.getKey(), endpoint, data);
    }

    public JSONObject put(String endpoint, Map<String, Object> data) {
        return sendRequest(HttpMethodEnum.PUT_REQUEST.getKey(), endpoint, data);
    }

    public JSONObject patch(String endpoint, Map<String, Object> data) {
        return sendRequest(HttpMethodEnum.PATCH_REQUEST.getKey(), endpoint, data);
    }

    public JSONObject delete(String endpoint) {
        return sendRequest(HttpMethodEnum.DELETE_REQUEST.getKey(), endpoint, null);
    }

    /**
     * 发送HTTP请求
     */
    private JSONObject sendRequest(String method, String endpoint, Map<String, Object> data) {
        try {
            String url = baseUrl + endpoint;
            
            // 获取有效的认证头
            TokenValidationResult validation = validateToken();
            Map<String, String> headers = buildAuthHeaders(validation.getAuthType(), validation.getValidToken());
            
            // 如果是access_token方式，需要添加到URL中
            if ("access_token".equals(validation.getAuthType())) {
                if (url.contains("?")) {
                    url += "&access_token=" + validation.getValidToken();
                } else {
                    url += "?access_token=" + validation.getValidToken();
                }
            }

            String responseBody = HttpRequestUtil.getDataByRequest(url, method, headers, JSONUtil.toJsonStr(data));
            return JSONUtil.parseObj(responseBody);
        } catch (Exception e) {
            log.error("GitCode API请求异常", e);
            throw new CustomException("GitCode API请求异常: " + e.getMessage());
        }
    }

    /**
     * 创建Issue
     * 根据GitCode v5 API: POST /api/v5/repos/:owner/:repo/issues
     */
    public JSONObject createIssue(String title, String description, String assigneeId, String labels) {
        String endpoint = "/repos/" + getOwner() + "/" + getRepo() + "/issues";
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", description);  // v5 API使用body而不是description
        if (StrUtil.isNotEmpty(assigneeId)) {
            data.put("assignees", new String[]{assigneeId});  // v5 API使用assignees数组
        }
        if (StrUtil.isNotEmpty(labels)) {
            data.put("labels", labels.split(","));  // v5 API使用labels数组
        }
        return post(endpoint, data);
    }

    /**
     * 更新Issue
     * 根据GitCode v5 API: PATCH /api/v5/repos/:owner/:repo/issues/:number
     */
    public JSONObject updateIssue(String issueNumber, Map<String, Object> data) {
        String endpoint = "/repos/" + getOwner() + "/" + getRepo() + "/issues/" + issueNumber;

        // 转换参数格式以符合v5 API
        Map<String, Object> v5Data = new HashMap<>();
        if (data.containsKey("title")) {
            v5Data.put("title", data.get("title"));
        }
        if (data.containsKey("description")) {
            v5Data.put("body", data.get("description"));  // v5使用body
        }
        if (data.containsKey("state")) {
            v5Data.put("state", data.get("state"));
        }
        if (data.containsKey("assigneeId")) {
            String assigneeId = (String) data.get("assigneeId");
            if (StrUtil.isNotEmpty(assigneeId)) {
                v5Data.put("assignees", new String[]{assigneeId});
            }
        }
        if (data.containsKey("labels")) {
            String labels = (String) data.get("labels");
            if (StrUtil.isNotEmpty(labels)) {
                v5Data.put("labels", labels.split(","));
            }
        }

        return patch(endpoint, v5Data);
    }

    /**
     * 删除Issue
     * 根据GitCode v5 API: DELETE /api/v5/repos/:owner/:repo/issues/:number
     */
    public JSONObject deleteIssue(String issueNumber) {
        String endpoint = "/repos/" + getOwner() + "/" + getRepo() + "/issues/" + issueNumber;
        return delete(endpoint);
    }

    /**
     * 创建Issue评论
     * 根据GitCode v5 API: POST /api/v5/repos/:owner/:repo/issues/:number/comments
     */
    public JSONObject createIssueComment(String issueNumber, String body) {
        String endpoint = "/repos/" + getOwner() + "/" + getRepo() + "/issues/" + issueNumber + "/comments";
        Map<String, Object> data = new HashMap<>();
        data.put("body", body);
        return post(endpoint, data);
    }

    /**
     * 更新Issue评论
     * 根据GitCode v5 API: PATCH /api/v5/repos/:owner/:repo/issues/:number/comments/:id
     */
    public JSONObject updateIssueComment(String issueNumber, String commentId, String body) {
        String endpoint = "/repos/" + getOwner() + "/" + getRepo() + "/issues/" + issueNumber + "/comments/" + commentId;
        Map<String, Object> data = new HashMap<>();
        data.put("body", body);
        return patch(endpoint, data);
    }

    /**
     * 删除Issue评论
     * 根据GitCode v5 API: DELETE /api/v5/repos/:owner/:repo/issues/:number/comments/:id
     */
    public JSONObject deleteIssueComment(String issueNumber, String commentId) {
        String endpoint = "/repos/" + getOwner() + "/" + getRepo() + "/issues/" + issueNumber + "/comments/" + commentId;
        return delete(endpoint);
    }

    /**
     * 给仓库点Star
     * 根据GitCode v5 API: PUT /api/v5/user/starred/:owner/:repo
     */
    public JSONObject starRepository() {
        String endpoint = "/user/starred/" + getOwner() + "/" + getRepo();
        return put(endpoint, null);
    }

    /**
     * 取消仓库Star
     * 根据GitCode v5 API: DELETE /api/v5/user/starred/:owner/:repo
     */
    public JSONObject unstarRepository() {
        String endpoint = "/user/starred/" + getOwner() + "/" + getRepo();
        return delete(endpoint);
    }

    /**
     * 检查用户是否已Star仓库
     * 根据GitCode v5 API: GET /api/v5/user/starred/:owner/:repo
     */
    public boolean isRepositoryStarred() {
        try {
            String endpoint = "/user/starred/" + getOwner() + "/" + getRepo();
            JSONObject response = get(endpoint);
            // 如果返回200状态，说明已经starred
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            log.warn("检查仓库Star状态失败", e);
            return false;
        }
    }

    /**
     * 上传图片到仓库
     * 根据GitCode v5 API: POST /api/v5/repos/:owner/:repo/img/upload
     */
    public JSONObject uploadImage(String base64Data, String fileName) {
        try {
            String endpoint = "/repos/" + getOwner() + "/" + getRepo() + "/img/upload";

            // 构建上传数据 - 使用base64编码
            Map<String, Object> uploadData = new HashMap<>();
            uploadData.put("img", base64Data);  // base64编码的图片数据
            uploadData.put("name", fileName);   // 图片名称

            // 调用POST方法上传文件
            JSONObject result = post(endpoint, uploadData);

            if (result != null && !result.isEmpty()) {
                // 构建返回结果
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("url", result.getStr("url"));
                responseData.put("alt", result.getStr("alt") != null ? result.getStr("alt") : fileName);
                responseData.put("markdown", result.getStr("markdown") != null ? result.getStr("markdown") : "![图片](" + result.getStr("url") + ")");

                return JSONUtil.parseObj(responseData);
            }
        } catch (Exception e) {
            log.error("上传图片失败", e);
            throw new CustomException("上传图片失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取仓库所有者
     */
    private String getOwner() {
        // 从projectUrl中提取owner，例如：https://gitcode.com/doc_wei/erp-pro -> doc_wei
        if (StrUtil.isNotEmpty(projectUrl)) {
            String[] parts = projectUrl.split("/");
            if (parts.length >= 4) {
                return parts[3]; // doc_wei
            }
        }
        return "doc_wei"; // 默认值
    }

    /**
     * 获取仓库名称
     */
    private String getRepo() {
        // 从projectUrl中提取repo，例如：https://gitcode.com/doc_wei/erp-pro -> erp-pro
        if (StrUtil.isNotEmpty(projectUrl)) {
            String[] parts = projectUrl.split("/");
            if (parts.length >= 5) {
                return parts[4]; // erp-pro
            }
        }
        return "erp-pro"; // 默认值
    }

    /**
     * Token验证结果类
     */
    public static class TokenValidationResult {
        private boolean valid;
        private String validToken;
        private String authType;
        private String errorMessage;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getValidToken() {
            return validToken;
        }

        public void setValidToken(String validToken) {
            this.validToken = validToken;
        }

        public String getAuthType() {
            return authType;
        }

        public void setAuthType(String authType) {
            this.authType = authType;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.enumeration.HttpMethodEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.HttpRequestUtil;
import com.skyeye.doc.member.entity.DocMember;
import com.skyeye.doc.member.service.DocMemberService;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DocMemberService docMemberService;

    private String baseUrl = "https://api.gitcode.com/api/v5";

    public String projectUrl = "https://gitcode.com/doc_wei/erp-pro";

    public String owner = "doc_wei";

    public String repo = "erp-pro";

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
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            DocMember docMember = docMemberService.selectById(userId);

            OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, data != null ? JSONUtil.toJsonStr(data) : StrUtil.EMPTY);
            Request request = new Request.Builder()
                .url(url)
                .method(method, body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + docMember.getGitcodeToken())
                .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            if (response.code() == 200) {
                if (StrUtil.isNotBlank(responseBody)) {
                    return JSONUtil.parseObj(responseBody);
                } else {
                    return new JSONObject();
                }
            }

            // 设置请求头
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/json");
//            headers.put("Accept", "application/json");
//            String userId = InputObject.getLogParamsStatic().get("id").toString();
//            DocMember docMember = docMemberService.selectById(userId);
//            if (StrUtil.isNotEmpty(docMember.getGitcodeToken())) {
//                headers.put("Authorization", "Bearer " + docMember.getGitcodeToken());
//            }
//
//            String responseBody = HttpRequestUtil.getDataByRequest(url, method, headers, JSONUtil.toJsonStr(data));
            log.info("GitCode API请求响应: {}", responseBody);
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
        String endpoint = "/repos/" + owner + "/issues";
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", description);  // v5 API使用body而不是description
        if (StrUtil.isNotEmpty(assigneeId)) {
            data.put("assignees", new String[]{assigneeId});  // v5 API使用assignees数组
        }
        if (StrUtil.isNotEmpty(labels)) {
            data.put("labels", labels.split(","));  // v5 API使用labels数组
        }
        data.put("repo", repo);
        return post(endpoint, data);
    }

    /**
     * 更新Issue
     * 根据GitCode v5 API: PATCH /api/v5/repos/:owner/:repo/issues/:number
     */
    public JSONObject updateIssue(String issueNumber, Map<String, Object> data) {
        String endpoint = "/repos/" + owner + "/issues/" + issueNumber;

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
        v5Data.put("repo", repo);
        return patch(endpoint, v5Data);
    }

    /**
     * 创建Issue评论
     * 根据GitCode v5 API: POST /api/v5/repos/:owner/:repo/issues/:number/comments
     */
    public JSONObject createIssueComment(String issueNumber, String body) {
        String endpoint = "/repos/" + owner + "/" + repo + "/issues/" + issueNumber + "/comments";
        Map<String, Object> data = new HashMap<>();
        data.put("body", body);
        return post(endpoint, data);
    }

    /**
     * 更新Issue评论
     * 根据GitCode v5 API: PATCH /api/v5/repos/:owner/:repo/issues/:number/comments/:id
     */
    public JSONObject updateIssueComment(String issueNumber, String commentId, String body) {
        String endpoint = "/repos/" + owner + "/" + repo + "/issues/comments/" + commentId;
        Map<String, Object> data = new HashMap<>();
        data.put("body", body);
        return patch(endpoint, data);
    }

    /**
     * 删除Issue评论
     * 根据GitCode v5 API: DELETE /api/v5/repos/:owner/:repo/issues/:number/comments/:id
     */
    public JSONObject deleteIssueComment(String issueNumber, String commentId) {
        String endpoint = "/repos/" + owner + "/" + repo + "/issues/comments/" + commentId;
        return delete(endpoint);
    }

    /**
     * 上传图片到仓库
     * 根据GitCode v5 API: POST /api/v5/repos/:owner/:repo/img/upload
     */
    public JSONObject uploadImage(String base64Data, String fileName) {
        try {
            String endpoint = "/repos/" + owner + "/" + repo + "/img/upload";

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
     * 验证Authorization Token
     * 使用 /api/v5/user 端点验证
     */
    public boolean validateAuthorizationToken(String token) {
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
}

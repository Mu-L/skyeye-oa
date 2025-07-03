/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync.http;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.enumeration.HttpMethodEnum;
import com.skyeye.common.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: HttpClientRequest
 * @Description: HTTP请求客户端封装
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 10:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public class HttpClientRequest {

    /**
     * 默认的请求头
     */
    private Map<String, String> defaultHeaders;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * 构造函数
     *
     * @param baseUrl 基础URL
     */
    public HttpClientRequest(String baseUrl) {
        this.baseUrl = baseUrl;
        this.defaultHeaders = new HashMap<>();
        // 设置默认的请求头
        defaultHeaders.put("Content-Type", "application/json; charset=UTF-8");
        defaultHeaders.put("Accept", "application/json");
    }

    /**
     * 设置默认请求头
     *
     * @param key   请求头键
     * @param value 请求头值
     * @return 当前对象
     */
    public HttpClientRequest setDefaultHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
        return this;
    }

    /**
     * 设置多个默认请求头
     *
     * @param headers 请求头Map
     * @return 当前对象
     */
    public HttpClientRequest setDefaultHeaders(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.defaultHeaders.putAll(headers);
        }
        return this;
    }

    /**
     * 获取完整URL
     *
     * @param path 路径
     * @return 完整URL
     */
    private String getFullUrl(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }

        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl + path.substring(1);
        } else if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        } else {
            return baseUrl + path;
        }
    }

    /**
     * 合并请求头
     *
     * @param headers 自定义请求头
     * @return 合并后的请求头
     */
    private Map<String, String> mergeHeaders(Map<String, String> headers) {
        Map<String, String> mergedHeaders = new HashMap<>(defaultHeaders);
        if (headers != null && !headers.isEmpty()) {
            mergedHeaders.putAll(headers);
        }
        return mergedHeaders;
    }

    /**
     * 发送GET请求
     *
     * @param path 请求路径
     * @return 响应结果
     */
    public String get(String path) {
        return get(path, null, null);
    }

    /**
     * 发送GET请求
     *
     * @param path   请求路径
     * @param params 请求参数
     * @return 响应结果
     */
    public String get(String path, Map<String, Object> params) {
        return get(path, params, null);
    }

    /**
     * 发送GET请求
     *
     * @param path    请求路径
     * @param params  请求参数
     * @param headers 请求头
     * @return 响应结果
     */
    public String get(String path, Map<String, Object> params, Map<String, String> headers) {
        String fullUrl = getFullUrl(path);
        Map<String, String> mergedHeaders = mergeHeaders(headers);
        String requestBody = params != null ? JSONUtil.toJsonStr(params) : null;

        log.debug("发送GET请求，URL: {}, 参数: {}, 请求头: {}", fullUrl, requestBody, mergedHeaders);
        String response = HttpRequestUtil.getDataByRequest(fullUrl, HttpMethodEnum.GET_REQUEST.getKey(), mergedHeaders, requestBody);
        log.debug("GET请求响应: {}", response);

        return response;
    }

    /**
     * 发送POST请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @return 响应结果
     */
    public String post(String path, Object body) {
        return post(path, body, null);
    }

    /**
     * 发送POST请求
     *
     * @param path    请求路径
     * @param body    请求体
     * @param headers 请求头
     * @return 响应结果
     */
    public String post(String path, Object body, Map<String, String> headers) {
        String fullUrl = getFullUrl(path);
        Map<String, String> mergedHeaders = mergeHeaders(headers);
        String requestBody = body != null ? JSONUtil.toJsonStr(body) : null;

        log.debug("发送POST请求，URL: {}, 请求体: {}, 请求头: {}", fullUrl, requestBody, mergedHeaders);
        String response = HttpRequestUtil.getDataByRequest(fullUrl, HttpMethodEnum.POST_REQUEST.getKey(), mergedHeaders, requestBody);
        log.debug("POST请求响应: {}", response);

        return response;
    }

    /**
     * 发送PUT请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @return 响应结果
     */
    public String put(String path, Object body) {
        return put(path, body, null);
    }

    /**
     * 发送PUT请求
     *
     * @param path    请求路径
     * @param body    请求体
     * @param headers 请求头
     * @return 响应结果
     */
    public String put(String path, Object body, Map<String, String> headers) {
        String fullUrl = getFullUrl(path);
        Map<String, String> mergedHeaders = mergeHeaders(headers);
        String requestBody = body != null ? JSONUtil.toJsonStr(body) : null;

        log.debug("发送PUT请求，URL: {}, 请求体: {}, 请求头: {}", fullUrl, requestBody, mergedHeaders);
        String response = HttpRequestUtil.getDataByRequest(fullUrl, HttpMethodEnum.PUT_REQUEST.getKey(), mergedHeaders, requestBody);
        log.debug("PUT请求响应: {}", response);

        return response;
    }

    /**
     * 发送DELETE请求
     *
     * @param path 请求路径
     * @return 响应结果
     */
    public String delete(String path) {
        return delete(path, null, null);
    }

    /**
     * 发送DELETE请求
     *
     * @param path   请求路径
     * @param params 请求参数
     * @return 响应结果
     */
    public String delete(String path, Map<String, Object> params) {
        return delete(path, params, null);
    }

    /**
     * 发送DELETE请求
     *
     * @param path    请求路径
     * @param params  请求参数
     * @param headers 请求头
     * @return 响应结果
     */
    public String delete(String path, Map<String, Object> params, Map<String, String> headers) {
        String fullUrl = getFullUrl(path);
        Map<String, String> mergedHeaders = mergeHeaders(headers);
        String requestBody = params != null ? JSONUtil.toJsonStr(params) : null;

        log.debug("发送DELETE请求，URL: {}, 参数: {}, 请求头: {}", fullUrl, requestBody, mergedHeaders);
        String response = HttpRequestUtil.getDataByRequest(fullUrl, HttpMethodEnum.DELETE_REQUEST.getKey(), mergedHeaders, requestBody);
        log.debug("DELETE请求响应: {}", response);

        return response;
    }
} 
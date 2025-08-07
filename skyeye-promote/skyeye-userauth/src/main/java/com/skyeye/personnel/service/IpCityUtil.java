package com.skyeye.personnel.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * IP获取城市工具类
 * 使用ip-api.com免费API获取IP对应的城市信息
 * 
 * @author skyeye
 */
@Slf4j
public class IpCityUtil {

    /**
     * ip-api.com API地址
     */
    private static final String IP_API_URL = "http://ip-api.com/json/";

    /**
     * 本地缓存，避免重复查询
     */
    private static final ConcurrentHashMap<String, String> IP_CITY_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据IP获取城市名
     * 
     * @param ip IP地址
     * @return 城市名，如果获取失败返回null
     */
    public static String getCityByIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return null;
        }

        // 检查缓存
        String cachedCity = IP_CITY_CACHE.get(ip);
        if (cachedCity != null) {
            return cachedCity;
        }

        try {
            String url = IP_API_URL + ip + "?lang=zh-CN";
            String response = HttpUtil.get(url, 5000);
            
            if (StrUtil.isNotBlank(response)) {
                JSONObject json = JSONUtil.parseObj(response);
                if ("success".equals(json.getStr("status"))) {
                    String city = json.getStr("city");
                    if (StrUtil.isNotBlank(city)) {
                        // 缓存结果
                        IP_CITY_CACHE.put(ip, city);
                        return city;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取IP城市信息失败，IP: {}", ip, e);
        }

        return null;
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        IP_CITY_CACHE.clear();
        log.info("IP城市缓存已清除");
    }

    /**
     * 获取缓存大小
     * 
     * @return 缓存大小
     */
    public static int getCacheSize() {
        return IP_CITY_CACHE.size();
    }
} 
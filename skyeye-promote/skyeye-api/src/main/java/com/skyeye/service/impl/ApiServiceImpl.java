/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.ApiConstants;
import com.skyeye.common.enumeration.VerificationParamsEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.api.ApiMation;
import com.skyeye.eve.entity.search.SearchMation;
import com.skyeye.eve.service.SearchConfigService;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.rest.IServiceApiRest;
import com.skyeye.service.ApiMationService;
import com.skyeye.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private JedisClientService jedisClientService;

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private SearchConfigService searchConfigService;

    @Autowired
    private ApiMationService apiMationService;

    @Autowired
    private IServiceApiRest iServiceApiRest;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 获取接口列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllSysEveReqMapping(InputObject inputObject, OutputObject outputObject) {
        String appId = inputObject.getParams().get("appId").toString();
        String key = ApiConstants.getApiMicroservicesRedisCacheKey(appId, env);
        Map<String, Object> microservices = JSONUtil.toBean(jedisClientService.get(key), null);
        URI uri = getUri(microservices.get("springApplicationName").toString());

        List<Map<String, Object>> beans = ExecuteFeignClient.get(() -> iServiceApiRest.queryAllServiceApiList(uri)).getRows();
        beans.forEach(bean -> {
            // 注释作为标题
            bean.put("title", bean.get("val"));
        });
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    private URI getUri(String springApplicationName) {
        // 根据服务名获取服务实例
        List<ServiceInstance> allInstances = discoveryClient.getInstances(springApplicationName);
        if (CollectionUtils.isEmpty(allInstances)) {
            throw new CustomException(String.format(Locale.ROOT, "this service[%s] has no instance.", springApplicationName));
        }
        return RandomUtil.randomEle(allInstances).getUri();
    }

    /**
     * 获取接口详情
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryApiDetails(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String appId = map.get("appId").toString();
        String apiId = map.get("id").toString();
        String key = ApiConstants.getApiMicroservicesRedisCacheKey(appId, env);
        Map<String, Object> microservices = JSONUtil.toBean(jedisClientService.get(key), null);
        URI uri = getUri(microservices.get("springApplicationName").toString());

        Map<String, Object> apiMation = ExecuteFeignClient.get(() -> iServiceApiRest.queryServiceApiById(uri, apiId)).getBean();
        if (apiMation != null && !apiMation.isEmpty()) {
            String allUse = apiMation.get("allUse").toString();
            String s;
            if ("0".equals(allUse)) {
                s = "无需登录，无需授权即可访问。";
            } else if ("1".equals(allUse)) {
                s = "需要登录，需要授权才能访问。";
            } else if ("2".equals(allUse)) {
                s = "需要登录，但无需授权即可访问。";
            } else {
                s = "错误参数。";
            }
            // 权限说明
            apiMation.put("sq", s);
            apiMation.put("requestId", apiId);
            List<Map<String, Object>> params = (List<Map<String, Object>>) apiMation.get("list");
            params = params.stream().sorted(Comparator.comparing(bean -> bean.get("number").toString())).collect(Collectors.toList());
            apiMation.put("list", params);
            loadSearchConfig(appId, apiId, params, apiMation);

            loadApiParamsMation(appId, apiId, apiMation);
            outputObject.setBean(apiMation);
        } else {
            outputObject.setreturnMessage("该信息不存在。");
        }
    }

    /**
     * 获取api参数信息
     *
     * @param appId
     * @param apiId
     */
    private void loadApiParamsMation(String appId, String apiId, Map<String, Object> apiMation) {
        List<ApiMation> apiParamsList = apiMationService.queryApiMationByAppIdAndUrlId(appId, apiId);
        apiMation.put("apiParamsList", apiParamsList);
    }

    /**
     * 加载高级筛选json信息
     *
     * @param appId     appId
     * @param urlId     接口id
     * @param params    入参信息
     * @param apiMation api信息
     */
    private void loadSearchConfig(String appId, String urlId, List<Map<String, Object>> params, Map<String, Object> apiMation) {
        Map<String, Object> pageParam = params.stream().filter(bean -> "page".equals(bean.get("name").toString())).findFirst().orElse(null);
        Map<String, Object> limitParam = params.stream().filter(bean -> "limit".equals(bean.get("name").toString())).findFirst().orElse(null);
        apiMation.put("advancedSearch", false);
        if (!CollectionUtils.isEmpty(pageParam) && !CollectionUtils.isEmpty(limitParam)) {
            apiMation.put("advancedSearch", true);
            // 存在分页参数，择取获取高级筛选json
            SearchMation searchMation = searchConfigService.querySearchMation(urlId, appId);
            if (searchMation != null) {
                apiMation.put("searchParams", searchMation.getParamsConfig());
                apiMation.put("searchParamsId", searchMation.getId());
            }
        }
    }

    /**
     * 获取限制条件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryLimitRestrictions(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = VerificationParamsEnum.getList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取所有微服务列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryApiMicroservices(InputObject inputObject, OutputObject outputObject) {
        List<String> keys = jedisClientService.getKeyListByPattern(ApiConstants.API_MICROSERVICES_REDIS_CACHE_KEY + "*");
        List<Map<String, Object>> result = new ArrayList<>();
        keys.forEach(key -> {
            if (key.endsWith(env)) {
                result.add(JSONUtil.toBean(jedisClientService.get(key), null));
            }
        });
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }
}

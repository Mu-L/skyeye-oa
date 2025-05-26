/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.service.impl;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.ReportConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.service.ReportCommonService;
import com.skyeye.common.util.HttpRequestUtil;
import com.skyeye.database.service.ReportDataBaseService;
import com.skyeye.eve.entity.ReportDataSource;
import com.skyeye.eve.entity.ReportMetaDataColumn;
import com.skyeye.sql.query.factory.QueryerFactory;
import net.sf.json.JSONArray;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportCommonServiceImpl
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/17 21:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "公共接口", groupName = "公共接口", manageShow = false)
public class ReportCommonServiceImpl implements ReportCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCommonServiceImpl.class);

    @Autowired
    private ReportDataBaseService reportDataBaseService;

    @Override
    public void testConnection(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String driverClass = params.get("driverClass").toString();
        String url = params.get("url").toString();
        String user = params.get("user").toString();
        String pass = params.containsKey("pass") ? params.get("pass").toString() : "";
        connectionDataBase(driverClass, url, user, pass, outputObject);
    }

    /**
     * 连接数据源
     *
     * @param driverClass  数据源驱动类
     * @param url          数据源连接字符串
     * @param user         用户名
     * @param password     密码
     * @param outputObject 出参以及提示信息的返回值对象
     * @return
     */
    @Override
    public boolean connectionDataBase(final String driverClass, final String url, final String user, final String password, OutputObject outputObject) {
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, user, password);
            return true;
        } catch (final Exception e) {
            LOGGER.warn("testConnection", e);
            if (outputObject != null) {
                outputObject.setreturnMessage(e.getMessage());
            }
            return false;
        } finally {
            this.releaseConnection(conn);
        }
    }

    @Override
    public void parseXmlText(InputObject inputObject, OutputObject outputObject) {
        Element rootElement;
        try {
            Map<String, Object> inputParams = inputObject.getParams();
            // 获取xml文件
            Document document = DocumentHelper.parseText(inputParams.get("xmlText").toString());
            // 获取根目录
            rootElement = document.getRootElement();
        } catch (Exception ex) {
            LOGGER.info("该文本不符合xml文件格式, 故无法解析. ", ex);
            outputObject.setreturnMessage("该文本不符合xml文件格式, 故无法解析.");
            return;
        }
        Map<String, Object> resultBean = new HashMap<>();
        List<String> nodeList = new ArrayList<>();
        parseSubNode(rootElement.elements(), nodeList, rootElement.getName());
        resultBean.put("nodeArray", nodeList);
        outputObject.setBean(resultBean);
    }

    // 解析并拼接节点下所有子节点名称
    private void parseSubNode(List<DefaultElement> elements, List<String> nodeList, String name) {
        elements.forEach(ele -> parseSubNode(ele.elements(), nodeList, name.concat(".").concat(ele.getName())));
        if (elements.size() == 0) {
            nodeList.add(name);
        }
    }

    @Override
    public void parseJsonText(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> inputParams = inputObject.getParams();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(inputParams.get("jsonText").toString(), Map.class);
        Set<String> result = new HashSet<>();
        parseJsonSubNode(map, result, true, "");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("nodeArray", result);
        outputObject.setBean(resultMap);
    }

    /**
     * 解析Json并拼接节点下所有子节点名称
     *
     * @param paramMap    被解析的map
     * @param sets        存放所有解析后的节点名称信息
     * @param isFirstTime 是否首层调用
     * @param name        名称
     */
    @Override
    public void parseJsonSubNode(Map<String, Object> paramMap, Set<String> sets, boolean isFirstTime, String name) {
        if (paramMap == null || paramMap.isEmpty()) {
            // 处理空Map的情况
            if (!isFirstTime && !name.isEmpty()) {
                sets.add(name);
            }
            return;
        }

        Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
        String key;
        Object value;
        for (Map.Entry<String, Object> obj : entries) {
            key = obj.getKey();
            value = obj.getValue();
            String currentPath = getNewName(isFirstTime, name, key);

            if (value instanceof Map) {
                // 处理嵌套Map
                parseJsonSubNode((Map<String, Object>) value, sets, false, currentPath);
            } else if (value instanceof List) {
                List<Object> tempList = (List<Object>) value;
                // 使用[*]表示数组
                String arrayPath = currentPath + "[*]";

                // 处理空数组的情况
                if (tempList.isEmpty()) {
                    sets.add(arrayPath);
                    continue;
                }

                // 检查第一个元素类型，假设数组中的元素类型一致
                Object firstElement = tempList.get(0);

                if (firstElement instanceof Map) {
                    // 处理对象数组
                    for (Object object : tempList) {
                        if (object instanceof Map) {
                            parseJsonSubNode((Map<String, Object>) object, sets, false, arrayPath);
                        } else if (object != null) {
                            // 处理混合类型数组中的非Map元素
                            sets.add(arrayPath);
                        }
                    }
                } else if (firstElement instanceof List) {
                    // 处理嵌套数组
                    for (Object object : tempList) {
                        if (object instanceof List) {
                            List<Object> nestedList = (List<Object>) object;
                            String nestedArrayPath = arrayPath + "[*]";

                            // 处理空嵌套数组
                            if (nestedList.isEmpty()) {
                                sets.add(nestedArrayPath);
                                continue;
                            }

                            // 处理嵌套数组中的第一个元素
                            Object nestedFirstElement = nestedList.get(0);
                            if (nestedFirstElement instanceof Map) {
                                for (Object nestedObject : nestedList) {
                                    if (nestedObject instanceof Map) {
                                        parseJsonSubNode((Map<String, Object>) nestedObject, sets, false, nestedArrayPath);
                                    }
                                }
                            } else {
                                // 嵌套数组中是基本类型
                                sets.add(nestedArrayPath);
                            }
                        }
                    }
                } else {
                    // 基本类型数组
                    sets.add(arrayPath);
                }
            } else {
                // 基本类型值
                sets.add(currentPath);
            }
        }
    }

    // 根据是否首个节点后, 依照不同规则进行拼接字符
    private String getNewName(boolean isFirstTime, String name, String key) {
        return isFirstTime ? key : name.concat(".").concat(key);
    }

    /**
     * 释放数据源
     *
     * @param conn
     */
    private void releaseConnection(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (final SQLException ex) {
                LOGGER.warn("测试数据库连接后释放资源失败", ex);
            }
        }
    }

    @Override
    public void queryDataBaseMationList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = ReportConstants.DataBaseMation.getDataBaseMationList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取连接池类型
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryPoolMationList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = ReportConstants.PoolMation.getPoolMationList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void parseSQLText(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String sqlText = params.get("sqlText").toString();
        String dataBaseId = params.get("dataBaseId").toString();
        LOGGER.info("data base id is {}", dataBaseId);
        // 1.获取数据源信息
        ReportDataSource dataBase = reportDataBaseService.getReportDataSource(dataBaseId);
        // 2.获取查询的列信息
        List<ReportMetaDataColumn> dataColumns = QueryerFactory.create(dataBase).parseMetaDataColumns(sqlText);
        outputObject.setBeans(JSONArray.fromObject(dataColumns));
    }

    @Override
    public void parseRestText(InputObject inputObject, OutputObject outputObject) {
        try {
            String requestUrl = inputObject.getParams().get("requestUrl").toString();
            String requestMethod = inputObject.getParams().get("requestMethod").toString();
            String requestHeader = inputObject.getParams().get("requestHeader").toString();
            String requestBody = inputObject.getParams().get("requestBody").toString();
            // 请求头
            List<Map<String, Object>> array = JSONUtil.toList(requestHeader, null);
            Map<String, String> requestHeaderKey2Value = array.stream()
                .collect(Collectors.toMap(bean -> bean.get("headerKey").toString(), bean -> bean.get("headerValue").toString()));
            // 发送请求
            String responseData = HttpRequestUtil.getDataByRequest(requestUrl, requestMethod, requestHeaderKey2Value, requestBody);
            // 存放并解析响应结果
            Set<String> result = new HashSet<>();
            parseJsonSubNode(JSONUtil.toBean(responseData, Map.class), result, true, "");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("nodeArray", result);
            outputObject.setBean(resultMap);
        } catch (Exception ex) {
            LOGGER.info("接口解析失败.", ex);
            outputObject.setreturnMessage("接口解析失败.");
        }
    }

}

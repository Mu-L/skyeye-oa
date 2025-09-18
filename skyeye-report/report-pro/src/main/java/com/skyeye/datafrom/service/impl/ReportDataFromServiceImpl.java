/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.datafrom.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.HttpRequestUtil;
import com.skyeye.database.service.ReportDataBaseService;
import com.skyeye.datafrom.classenum.ReportDataFromType;
import com.skyeye.datafrom.dao.ReportDataFromDao;
import com.skyeye.datafrom.entity.ReportDataFrom;
import com.skyeye.datafrom.entity.ReportDataFromRest;
import com.skyeye.datafrom.service.*;
import com.skyeye.eve.entity.ReportDataSource;
import com.skyeye.eve.entity.ReportMetaDataRow;
import com.skyeye.sql.query.factory.QueryerFactory;
import com.skyeye.util.XmlExercise;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportDataFromServiceImpl
 * @Description: 数据来源服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/3 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "数据来源", groupName = "数据来源")
public class ReportDataFromServiceImpl extends SkyeyeBusinessServiceImpl<ReportDataFromDao, ReportDataFrom> implements ReportDataFromService {

    @Autowired
    private ReportDataFromJsonService reportDataFromJsonService;

    @Autowired
    private ReportDataFromRestService reportDataFromRestService;

    @Autowired
    private ReportDataFromSQLService reportDataFromSQLService;

    @Autowired
    private ReportDataFromXMLService reportDataFromXMLService;

    @Autowired
    private ReportDataBaseService reportDataBaseService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        beans.forEach(bean -> {
            bean.put("typeName", ReportDataFromType.getNameByType(Integer.parseInt(bean.get("type").toString())));
        });
        return beans;
    }

    @Override
    public void updatePrepose(ReportDataFrom entity) {
        ReportDataFrom oldDataFrom = selectById(entity.getId());
        deletePostpose(oldDataFrom);
    }

    @Override
    public void writePostpose(ReportDataFrom entity, String userId) {
        super.writePostpose(entity, userId);
        if (entity.getType() == ReportDataFromType.XML.getKey()) {
            entity.getXmlEntity().setFromId(entity.getId());
            reportDataFromXMLService.createEntity(entity.getXmlEntity(), userId);
        } else if (entity.getType() == ReportDataFromType.JSON.getKey()) {
            entity.getJsonEntity().setFromId(entity.getId());
            reportDataFromJsonService.createEntity(entity.getJsonEntity(), userId);
        } else if (entity.getType() == ReportDataFromType.REST.getKey()) {
            entity.getRestEntity().setFromId(entity.getId());
            reportDataFromRestService.createEntity(entity.getRestEntity(), userId);
        } else if (entity.getType() == ReportDataFromType.SQL.getKey()) {
            entity.getSqlEntity().setFromId(entity.getId());
            reportDataFromSQLService.createEntity(entity.getSqlEntity(), userId);
        }
    }

    @Override
    public void deletePostpose(ReportDataFrom entity) {
        if (entity.getType() == ReportDataFromType.XML.getKey()) {
            reportDataFromXMLService.deleteByFromId(entity.getId());
        } else if (entity.getType() == ReportDataFromType.JSON.getKey()) {
            reportDataFromJsonService.deleteByFromId(entity.getId());
        } else if (entity.getType() == ReportDataFromType.REST.getKey()) {
            reportDataFromRestService.deleteByFromId(entity.getId());
        } else if (entity.getType() == ReportDataFromType.SQL.getKey()) {
            reportDataFromSQLService.deleteByFromId(entity.getId());
        }
    }

    @Override
    public ReportDataFrom selectById(String id) {
        ReportDataFrom reportDataFrom = super.selectById(id);
        if (reportDataFrom.getType() == ReportDataFromType.XML.getKey()) {
            reportDataFrom.setXmlEntity(reportDataFromXMLService.getByFromId(id));
        } else if (reportDataFrom.getType() == ReportDataFromType.JSON.getKey()) {
            reportDataFrom.setJsonEntity(reportDataFromJsonService.getByFromId(id));
        } else if (reportDataFrom.getType() == ReportDataFromType.REST.getKey()) {
            reportDataFrom.setRestEntity(reportDataFromRestService.getByFromId(id));
        } else if (reportDataFrom.getType() == ReportDataFromType.SQL.getKey()) {
            reportDataFrom.setSqlEntity(reportDataFromSQLService.getByFromId(id));
        }
        return reportDataFrom;
    }

    /**
     * 根据数据来源id获取该数据来源下的所有数据并组装成map
     *
     * @param fromId      数据来源id
     * @param needGetKeys 需要获取的key
     * @param inputParams 入参
     * @return 该数据来源下的所有数据并组装成map
     */
    @Override
    public Map<String, Object> getReportDataFromMapByFromId(String fromId, List<String> needGetKeys, String inputParams) {
        String jsonContent = getJsonStrByFromId(fromId, inputParams);
        Map<String, Object> result = new HashMap<>();
        needGetKeys.forEach(key -> {
            Object value = JsonPath.read(jsonContent, String.format(Locale.ROOT, "$.%s", key));
            result.put(key, value);
        });
        result.put("allData", JSONUtil.toBean(jsonContent, null));
        return result;
    }

    /**
     * 根据数据来源id获取数据并转换成json串
     *
     * @param fromId 数据来源id
     * @return 获取数据并转换成json串
     */
    private String getJsonStrByFromId(String fromId, String inputParams) {
        // 根据dataFromId获取对应type
        ReportDataFrom reportDataFrom = selectById(fromId);
        if (ObjectUtil.isNotEmpty(reportDataFrom)) {
            if (reportDataFrom.getType() == ReportDataFromType.XML.getKey()) {
                return XmlExercise.xml2json(reportDataFrom.getXmlEntity().getXmlContent());
            } else if (reportDataFrom.getType() == ReportDataFromType.JSON.getKey()) {
                return reportDataFrom.getJsonEntity().getJsonContent();
            } else if (reportDataFrom.getType() == ReportDataFromType.REST.getKey()) {
                ReportDataFromRest restEntity = reportDataFrom.getRestEntity();
                Map<String, String> requestHeaderKey2Value = restEntity.getHeader().stream()
                    .collect(Collectors.toMap(bean -> bean.get("headerKey").toString(), bean -> bean.get("headerValue").toString()));
                if (tenantEnable) {
                    requestHeaderKey2Value.put("tenantId", TenantContext.getTenantId());
                }
                String userToken = GetUserToken.getUserToken(InputObject.getRequest());
                requestHeaderKey2Value.put("userToken", userToken);

                String responseData = HttpRequestUtil.getDataByRequest(restEntity.getRestUrl(), restEntity.getMethod(), requestHeaderKey2Value, restEntity.getRequestBody());
                return responseData;
            } else if (reportDataFrom.getType() == ReportDataFromType.SQL.getKey()) {
                // 1.获取数据源信息
                ReportDataSource dataBase = reportDataBaseService.getReportDataSource(reportDataFrom.getSqlEntity().getDataBaseId());
                List<ReportMetaDataRow> metaDataRows = QueryerFactory.create(dataBase).getMetaDataRows(reportDataFrom.getSqlEntity().getSqlContent());
                Map<String, Object> result = new HashMap<>();
                result.put("data", resetSqlResultData(metaDataRows));
                return JSON.toJSONString(result);
            }
        }
        return "{}";
    }

    private List<Map<String, Object>> resetSqlResultData(List<ReportMetaDataRow> metaDataRows) {
        List<Map<String, Object>> result = new ArrayList<>();
        metaDataRows.forEach(cells -> {
            Map<String, Object> bean = new HashMap<>();
            cells.getCells().forEach((key, cell) -> {
                bean.put(key, cell.getValue());
            });
            result.add(bean);
        });
        return result;
    }

    /**
     * 根据数据来源信息获取要取的数据
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryReportDataFromMationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 根据数据来源id获取解析对应的数据
        String fromId = params.get("id").toString();
        // 前台需要获取的数据json
        Map<String, Object> needGetData = JSONObject.fromObject(params.get("needGetDataStr").toString());
        List<String> needGetKeys = needGetData.entrySet().stream().map(bean -> bean.getKey()).collect(Collectors.toList());
        // 入参
        String inputParams = params.get("inputParams").toString();

        Map<String, Object> data = getReportDataFromMapByFromId(fromId, needGetKeys, inputParams);
        Map<String, Object> result = new HashMap<>();
        needGetData.forEach((key, value) -> {
            if (data.containsKey(key)) {
                result.put(key, data.get(key));
            }
        });
        outputObject.setBean(result);
    }

}

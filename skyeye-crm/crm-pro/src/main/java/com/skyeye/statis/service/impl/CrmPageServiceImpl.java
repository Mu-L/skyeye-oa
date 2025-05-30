/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.statis.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.service.ISysDictDataService;
import com.skyeye.statis.dao.CrmPageDao;
import com.skyeye.statis.service.CrmPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CrmPageServiceImpl implements CrmPageService {

    @Autowired
    private CrmPageDao crmPageDao;

    @Autowired
    private ISysDictDataService iSysDictDataService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void queryInsertNumByYear(InputObject inputObject, OutputObject outputObject) {
        String year = inputObject.getParams().get("year").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = crmPageDao.queryInsertNumByYear(year, tenantId);
        outputObject.setBeans(beans);
    }

    @Override
    public void queryCustomNumByOtherType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String crmCustomerType = params.get("crmCustomerType").toString();
        String crmCustomerFrom = params.get("crmCustomerFrom").toString();
        String crmCustomerIndustry = params.get("crmCustomerIndustry").toString();
        String crmCustomerGroup = params.get("crmCustomerGroup").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 1.根据客户分类统计客户数量
        List<Map<String, Object>> numType = this.getDictDataNun(crmCustomerType, crmPageDao.queryCustomNumByType(tenantId));
        // 2.根据客户来源统计客户数量
        List<Map<String, Object>> numFrom = this.getDictDataNun(crmCustomerFrom, crmPageDao.queryCustomNumByFrom(tenantId));
        // 3.根据所属行业统计客户数量
        List<Map<String, Object>> numIndustry = this.getDictDataNun(crmCustomerIndustry, crmPageDao.queryCustomNumByIndustry(tenantId));
        // 4.根据客户分组统计客户数量
        List<Map<String, Object>> numGroup = this.getDictDataNun(crmCustomerGroup, crmPageDao.queryCustomNumByGroup(tenantId));
        Map<String, Object> map = new HashMap<>();
        map.put("numType", numType);
        map.put("numFrom", numFrom);
        map.put("numIndustry", numIndustry);
        map.put("numGroup", numGroup);
        outputObject.setBean(map);
    }

    private List<Map<String, Object>> getDictDataNun(String code, List<Map<String, Object>> numDataFrom) {
        List<Map<String, Object>> dictDataList = iSysDictDataService.queryDictDataListByDictTypeCode(code);
        Map<String, String> numDataMap = numDataFrom.stream()
            .collect(Collectors.toMap(bean -> bean.get("dictDataId").toString(), bean -> bean.get("number").toString()));
        if (!CollectionUtils.isEmpty(dictDataList)) {
            dictDataList.forEach(bean -> {
                String num = numDataMap.get(bean.get("id").toString());
                if (!ToolUtil.isBlank(num)) {
                    bean.put("number", num);
                } else {
                    bean.put("number", CommonNumConstants.NUM_ZERO);
                }
            });
        }
        return dictDataList;
    }

    @Override
    public void queryCustomDocumentaryType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
        String crmDocumentaryType = params.get("crmDocumentaryType").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = this.getDictDataNun(crmDocumentaryType, crmPageDao.queryCustomDocumentaryType(year, tenantId));
        outputObject.setBeans(beans);
    }

    @Override
    public void queryNewContractNum(InputObject inputObject, OutputObject outputObject) {
        String year = inputObject.getParams().get("year").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = crmPageDao.queryNewContractNum(year, tenantId);
        outputObject.setBeans(beans);
    }

    @Override
    public void queryNewDocumentaryNum(InputObject inputObject, OutputObject outputObject) {
        String year = inputObject.getParams().get("year").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = crmPageDao.queryNewDocumentaryNum(year, tenantId);
        outputObject.setBeans(beans);
    }


}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.ActGroupUserService;
import com.skyeye.eve.service.ISysDictDataService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActivitiUserServiceImpl
 * @Description: 工作流用户相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiUserServiceImpl implements ActivitiUserService {

    @Autowired
    protected ISysDictDataService iSysDictDataService;

    @Autowired
    private ActGroupUserService actGroupUserService;

    @Override
    @IgnoreTenant
    public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String reqObjStr = map.get("reqObj").toString();
        Map<String, Object> reqObj = JSONUtil.toBean(reqObjStr, null);

        // 获取参数信息
        List<Map<String, Object>> conditions = JSONUtil.toList(reqObj.get("conditions").toString(), null);

        // 查询参数
        CommonPageInfo commonPageInfo = new CommonPageInfo();
        setOtherParam(conditions, commonPageInfo);
        initPagingMation(reqObj, commonPageInfo);

        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Map<String, Object>> beans = actGroupUserService.queryPageDataList(commonPageInfo);
        long total = pages.getTotal();

        // 表信息
        Map<String, Object> query = new HashMap<>();
        setCommonUserTableElement(query, reqObj.get("queryId").toString(), commonPageInfo.getLimit());
        query.put("columnList", ActivitiConstants.getActivitiUserColumnList());
        query.put("columnMap", ActivitiConstants.getActivitiUserColumnMap());

        // 分页信息
        Map<String, Object> pageInfo = getTablePageMation(total, commonPageInfo);

        outputObject.setCustomBean("query", query);
        outputObject.setCustomBean("pageInfo", pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(total);
    }

    private static void setOtherParam(List<Map<String, Object>> conditions, CommonPageInfo commonPageInfo) {
        for (Map<String, Object> condition : conditions) {//参数信息
            String key = condition.get("key").toString();
            if (key.equals("groupId")) {
                commonPageInfo.setObjectId(condition.getOrDefault("value", StrUtil.EMPTY).toString());
            } else if (key.equals("name")) {
                commonPageInfo.setKeyword(condition.getOrDefault("value", StrUtil.EMPTY).toString());
            }
        }
    }

    /**
     * 人员选择的表格公共部分
     *
     * @param query   table信息
     * @param queryId 表格id
     * @param limit   每页多少条数据
     */
    private void setCommonUserTableElement(Map<String, Object> query, String queryId, Integer limit) {
        query.put("id", queryId);
        query.put("key", "id");
        query.put("tableName", "流程用户列表");
        query.put("pagesize", limit);
        query.put("pagesInGrp", "5");
        query.put("widthType", "px");
        query.put("allowPaging", true);
        query.put("enableMultiline", true);
        query.put("isServerFilter", false);
        query.put("enableMultiHeader", false);
        query.put("simpleSearch", false);
        query.put("startRow", 1);
    }

    /**
     * 初始化分页信息
     *
     * @param reqObj         请求参数信息
     * @param commonPageInfo 查询参数
     */
    private void initPagingMation(Map<String, Object> reqObj, CommonPageInfo commonPageInfo) {
        Map<String, Object> page = JSONObject.fromObject(reqObj.get("pageInfo").toString());
        if (CollectionUtil.isEmpty(page)) {
            commonPageInfo.setLimit(10);
            commonPageInfo.setPage(1);
        } else {
            commonPageInfo.setLimit(Integer.parseInt(page.get("pageSize").toString()));
            commonPageInfo.setPage(Integer.parseInt(page.get("pageNum").toString()));
        }
    }

    /**
     * 获取表格的分页信息
     *
     * @param total          数据总条数
     * @param commonPageInfo 分页信息
     * @return
     */
    private Map<String, Object> getTablePageMation(long total, CommonPageInfo commonPageInfo) {
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("pageNum", commonPageInfo.getPage());
        pageInfo.put("pageSize", commonPageInfo.getLimit());
        pageInfo.put("count", total);
        long pageCount = total / commonPageInfo.getLimit();
        if (total % commonPageInfo.getLimit() != 0) {
            pageCount++;
        }
        pageInfo.put("pageCount", pageCount);
        return pageInfo;
    }

    @Override
    @IgnoreTenant
    public void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String reqObjStr = map.get("reqObj").toString();
        Map<String, Object> reqObj = JSONUtil.toBean(reqObjStr, null);
        String queryId = reqObj.get("queryId").toString();
        //获取参数信息
        List<Map<String, Object>> conditions = JSONUtil.toList(reqObj.get("conditions").toString(), null);

        //查询参数
        CommonPageInfo commonPageInfo = new CommonPageInfo();
        setOtherParam(conditions, commonPageInfo);
        initPagingMation(reqObj, commonPageInfo);

        long total;
        List<Map<String, Object>> beans;
        if ("id_group_list".equals(queryId)) {
            // 分组
            beans = iSysDictDataService.queryDictDataListByDictTypeCode("ACT_GROUP");
            total = beans.size();
        } else {
            // 人员
            Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
            beans = actGroupUserService.queryPageDataList(commonPageInfo);
            total = pages.getTotal();
        }

        // 表信息
        Map<String, Object> query = new HashMap<>();
        setCommonUserTableElement(query, reqObj.get("queryId").toString(), commonPageInfo.getLimit());
        if ("id_group_list".equals(queryId)) {//分组
            query.put("columnList", ActivitiConstants.getActivitiGroupColumnList());
            query.put("columnMap", ActivitiConstants.getActivitiGroupColumnMap());
        } else {//人员
            query.put("columnList", ActivitiConstants.getActivitiUserColumnListByGroupId());
            query.put("columnMap", ActivitiConstants.getActivitiUserColumnMapByGroupId());
        }

        // 分页信息
        Map<String, Object> pageInfo = getTablePageMation(total, commonPageInfo);

        outputObject.setCustomBean("query", query);
        outputObject.setCustomBean("pageInfo", pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(total);
    }

}

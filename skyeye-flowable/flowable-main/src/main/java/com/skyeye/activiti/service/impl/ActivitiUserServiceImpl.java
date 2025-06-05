/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.dao.ActGroupUserDao;
import com.skyeye.eve.entity.ActGroupUser;
import com.skyeye.eve.service.ActGroupUserService;
import com.skyeye.eve.service.ISysDictDataService;
import net.sf.json.JSONObject;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ActGroupUserDao actGroupUserDao;

    @Autowired
    private IdentityService identityService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    @IgnoreTenant
    public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String reqObjStr = map.get("reqObj").toString();
        Map<String, Object> reqObj = JSONUtil.toBean(reqObjStr, null);

        // 获取参数信息
        List<Map<String, Object>> conditions = JSONUtil.toList(reqObj.get("conditions").toString(), null);

        // 查询参数
        Map<String, Object> parmter = new HashMap<>();
        if (conditions.size() > 0) {//参数信息
            parmter.put(conditions.get(0).get("key").toString(), conditions.get(0).get("value"));
        }
        initPagingMation(reqObj, parmter);

        Page pages = PageHelper.startPage(Integer.parseInt(parmter.get("page").toString()), Integer.parseInt(parmter.get("limit").toString()));
        if (tenantEnable) {
            parmter.put("tenantId", TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = actGroupUserDao.queryUserListToActiviti(parmter);
        long total = pages.getTotal();

        // 表信息
        Map<String, Object> query = new HashMap<>();
        setCommonUserTableElement(query, reqObj.get("queryId").toString(), parmter.get("limit").toString());
        query.put("columnList", ActivitiConstants.getActivitiUserColumnList());
        query.put("columnMap", ActivitiConstants.getActivitiUserColumnMap());

        // 分页信息
        Map<String, Object> pageInfo = getTablePageMation(total, parmter);

        outputObject.setCustomBean("query", query);
        outputObject.setCustomBean("pageInfo", pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(total);
    }

    /**
     * 人员选择的表格公共部分
     *
     * @param query   table信息
     * @param queryId 表格id
     * @param limit   每页多少条数据
     */
    private void setCommonUserTableElement(Map<String, Object> query, String queryId, String limit) {
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
     * @param reqObj  请求参数信息
     * @param parmter 查询参数
     */
    private void initPagingMation(Map<String, Object> reqObj, Map<String, Object> parmter) {
        Map<String, Object> page = JSONObject.fromObject(reqObj.get("pageInfo").toString());
        if (CollectionUtil.isEmpty(page)) {
            parmter.put("page", 1);
            parmter.put("limit", 10);
        } else {
            parmter.put("page", page.get("pageNum"));
            parmter.put("limit", page.get("pageSize"));
        }
    }

    /**
     * 获取表格的分页信息
     *
     * @param total   数据总条数
     * @param parmter 分页信息
     * @return
     */
    private Map<String, Object> getTablePageMation(long total, Map<String, Object> parmter) {
        Map<String, Object> pageInfo = new HashMap<>();
        String limit = parmter.get("limit").toString();
        pageInfo.put("pageNum", parmter.get("page"));
        pageInfo.put("pageSize", parmter.get("limit"));
        pageInfo.put("count", total);
        long pageCount = total / Integer.parseInt(limit);
        if (total % Integer.parseInt(limit) != 0) {
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
        Map<String, Object> parmter = new HashMap<>();
        for (Map<String, Object> condition : conditions) {//参数信息
            parmter.put(condition.get("key").toString(), condition.get("value"));
        }
        initPagingMation(reqObj, parmter);

        long total;
        List<Map<String, Object>> beans;
        if ("id_group_list".equals(queryId)) {
            // 分组
            beans = iSysDictDataService.queryDictDataListByDictTypeCode("ACT_GROUP");
            total = beans.size();
        } else {
            // 人员
            Page pages = PageHelper.startPage(Integer.parseInt(parmter.get("page").toString()), Integer.parseInt(parmter.get("limit").toString()));
            if (tenantEnable) {
                parmter.put("tenantId", TenantContext.getTenantId());
            }
            beans = actGroupUserDao.queryUserListToActivitiByGroup(parmter);
            total = pages.getTotal();
        }

        // 表信息
        Map<String, Object> query = new HashMap<>();
        setCommonUserTableElement(query, reqObj.get("queryId").toString(), parmter.get("limit").toString());
        if ("id_group_list".equals(queryId)) {//分组
            query.put("columnList", ActivitiConstants.getActivitiGroupColumnList());
            query.put("columnMap", ActivitiConstants.getActivitiGroupColumnMap());
        } else {//人员
            query.put("columnList", ActivitiConstants.getActivitiUserColumnListByGroupId());
            query.put("columnMap", ActivitiConstants.getActivitiUserColumnMapByGroupId());
        }

        // 分页信息
        Map<String, Object> pageInfo = getTablePageMation(total, parmter);

        outputObject.setCustomBean("query", query);
        outputObject.setCustomBean("pageInfo", pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(total);
    }

    /**
     * 用户以及用户组信息同步到act表中
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertSyncUserListMationToAct(InputObject inputObject, OutputObject outputObject) {
        // 同步用户组信息
        List<Map<String, Object>> groupList = iSysDictDataService.queryDictDataListByDictTypeCode("ACT_GROUP");
        for (Map<String, Object> bean : groupList) {
            Group group = identityService.newGroup(bean.get("id").toString());
            group.setName(bean.get("name").toString());
            identityService.deleteGroup(group.getId());
            identityService.saveGroup(group);
        }
        // 同步用户信息
        List<ActGroupUser> actGroupUsers = actGroupUserService.queryAllActGroupUser();
        for (ActGroupUser bean : actGroupUsers) {
            Map<String, Object> userMation = bean.getUserMation();
            if (CollectionUtil.isEmpty(userMation)) {
                continue;
            }
            User user = identityService.newUser(userMation.get("id").toString());
            user.setDisplayName(userMation.get("userName").toString());
            user.setFirstName(userMation.get("userName").toString());
            user.setLastName(userMation.get("userCode").toString());
            user.setEmail(userMation.get("email").toString());
            identityService.deleteUser(user.getId());
            identityService.saveUser(user);
            // 同步用户和用户组的关系信息
            identityService.deleteMembership(bean.getUserId(), bean.getGroupId());
            identityService.createMembership(bean.getUserId(), bean.getGroupId());
        }
    }

}

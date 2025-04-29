/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.organization.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.OvertimeSettlementType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.dao.CompanyDepartmentDao;
import com.skyeye.organization.entity.Department;
import com.skyeye.organization.service.CompanyDepartmentService;
import com.skyeye.organization.service.CompanyMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CompanyDepartmentServiceImpl
 * @Description: 公司部门信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "部门管理", groupName = "组织模块")
public class CompanyDepartmentServiceImpl extends SkyeyeBusinessServiceImpl<CompanyDepartmentDao, Department> implements CompanyDepartmentService {

    @Autowired
    private CompanyDepartmentDao companyDepartmentDao;

    @Autowired
    private CompanyMationService companyMationService;

    /**
     * 获取公司部门信息列表
     *
     * @param inputObject 入参以及用户信息等获取对象
     */
    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentList(commonPageInfo);
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        beans.forEach(bean -> {
            Integer overtimeSettlementType = Integer.parseInt(bean.get("overtimeSettlementType").toString());
            bean.put("overtimeSettlementTypeName", OvertimeSettlementType.getTitleByType(overtimeSettlementType));
        });
        return beans;
    }

    @Override
    public void deletePreExecution(String id) {
        // 判断是否有员工
        Map<String, Object> bean = companyDepartmentDao.queryCompanyDepartmentUserMationById(id);
        if (Integer.parseInt(bean.get("childsNum").toString()) > 0) {
            throw new CustomException("该部门下存在员工，无法直接删除。");
        }
        // 判断是否有职位
        bean = companyDepartmentDao.queryCompanyJobNumMationById(id);
        if (Integer.parseInt(bean.get("companyJobNum").toString()) > 0) {
            throw new CustomException("该部门下存在职位，无法直接删除。");
        }
    }

    /**
     * 获取公司部门信息列表展示为树根据公司id
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCompanyDepartmentListTreeByCompanyId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = companyDepartmentDao.queryCompanyDepartmentListTreeByCompanyId(map);
        beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 根据公司id获取部门列表展示为下拉选择框
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCompanyDepartmentListByCompanyIdToSelect(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String companyId = map.get("companyId").toString();
        List<Map<String, Object>> beans = queryDepartmentList(Arrays.asList(companyId), new ArrayList<>());
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取部门列表展示为表格供其他选择
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */

    @Override
    public void queryCompanyDepartmentListToChoose(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = queryPageDataList(inputObject);
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取当前登录用户所属企业的所有部门信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryDepartmentListByCurrentUserBelong(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> user = inputObject.getLogParams();
        String companyId = user.get("companyId").toString();
        List<Map<String, Object>> list = queryDepartmentList(Arrays.asList(companyId), new ArrayList<>());
        outputObject.setBean(user);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public List<Map<String, Object>> queryDepartmentList(List<String> companyIds, List<String> departmentIds) {
        companyIds = companyIds.stream().filter(str -> !ToolUtil.isBlank(str)).collect(Collectors.toList());
        departmentIds = departmentIds.stream().filter(str -> !ToolUtil.isBlank(str)).collect(Collectors.toList());
        List<Map<String, Object>> beans = companyDepartmentDao.queryDepartmentList(companyIds, departmentIds);
        return CollectionUtil.isNotEmpty(beans) ? beans : new ArrayList<>();
    }

}

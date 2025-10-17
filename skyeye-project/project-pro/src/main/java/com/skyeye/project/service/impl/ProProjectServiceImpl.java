/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.IContractService;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.erp.service.ISupplierContractService;
import com.skyeye.erp.service.ISupplierService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.project.classenum.ProjectStateEnum;
import com.skyeye.project.dao.ProProjectDao;
import com.skyeye.project.entity.Project;
import com.skyeye.project.service.ProProjectService;
import com.skyeye.sdk.catalog.service.CatalogSdkService;
import com.skyeye.team.service.ITeamBusinessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ProProjectServiceImpl
 * @Description: 项目管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "项目管理", groupName = "项目管理", flowable = true)
public class ProProjectServiceImpl extends SkyeyeBusinessServiceImpl<ProProjectDao, Project> implements ProProjectService, CatalogSdkService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ITeamBusinessService iTeamBusinessService;

    @Autowired
    private ICustomerService iCustomerService;

    @Autowired
    private ISupplierService iSupplierService;

    @Autowired
    private IContractService iContractService;

    @Autowired
    private ISupplierContractService iSupplierContractService;

    @Override
    public void queryProProjectList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.equals(commonPageInfo.getType(), "myCharge")) {
            // 我负责的
            ResultEntity resultEnt = iTeamBusinessService.queryMyBusinessTeamIdsLinkObjectId(commonPageInfo.getPage(),
                commonPageInfo.getLimit(), getServiceClassName(), true);
            if (CollectionUtil.isEmpty(resultEnt.getRows())) {
                throw new CustomException("您还不在任何团队中，请联系管理员");
            }
            List<String> ids = resultEnt.getRows().stream().map(row -> row.get("objectId").toString()).distinct().collect(Collectors.toList());
            QueryWrapper<Project> queryWrapper = getQueryWrapper(commonPageInfo);
            queryWrapper.in(CommonConstants.ID, ids);
            List<Project> projectList = list(queryWrapper);
            iAuthUserService.setName(projectList, "createId", "createName");
            iAuthUserService.setName(projectList, "lastUpdateId", "lastUpdateName");
            String serviceClassName = getServiceClassName();
            projectList.forEach(project -> {
                project.setServiceClassName(serviceClassName);
            });
            outputObject.setBeans(projectList);
            outputObject.settotal(resultEnt.getTotal());
        } else {
            // 我创建的 / 全部
            queryPageList(inputObject, outputObject);
        }
    }

    @Override
    public QueryWrapper<Project> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Project> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (StrUtil.equals("myCreate", commonPageInfo.getType())) {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(Project::getCreateId), userId);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        return beans;
    }

    @Override
    public void createPostpose(Project entity, String userId) {
        // 创建团队信息
        iTeamBusinessService.createTeamBusiness(entity.getTeamTemplateId(), entity.getId(), getServiceClassName());
    }

    @Override
    public void deletePostpose(String id) {
        iTeamBusinessService.deleteTeamBusiness(id, getServiceClassName());
    }

    @Override
    public void validatorEntity(Project entity) {
        if (StrUtil.isEmpty(entity.getNumberCode())) {
            return;
        }
        // 校验基础信息
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Project::getNumberCode), entity.getNumberCode());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Project checkProject = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkProject)) {
            throw new CustomException("该项目编号已存在.");
        }
    }

    @Override
    public Project selectById(String id) {
        Project project = super.selectById(id);
        // 部门
        iDepmentService.setDataMation(project, Project::getDepartmentId);
        // 往来单位
        Map<String, Object> contractMation = new HashMap<>();
        if (StrUtil.equals(project.getHolderKey(), CorrespondentEnterEnum.CUSTOM.getKey())) {
            project.setHolderMation(iCustomerService.queryDataMationById(project.getHolderId()));
            contractMation = iContractService.queryDataMationById(project.getContractId());
        } else if (StrUtil.equals(project.getHolderKey(), CorrespondentEnterEnum.SUPPLIER.getKey())) {
            project.setHolderMation(iSupplierService.queryDataMationById(project.getHolderId()));
            contractMation = iSupplierContractService.queryDataMationById(project.getContractId());
        }
        contractMation.put("name", contractMation.get("title"));
        project.setContractMation(contractMation);
        return project;
    }

    @Override
    public List<Project> selectByIds(String... ids) {
        List<Project> projects = super.selectByIds(ids);
        // 部门
        iDepmentService.setDataMation(projects, Project::getDepartmentId);
        // 往来单位
        // 供应商
        List<String> supplierIds = projects.stream()
            .filter(project -> StrUtil.equals(CorrespondentEnterEnum.SUPPLIER.getKey(), project.getHolderKey()))
            .map(Project::getHolderId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(supplierIds)) {
            Map<String, Map<String, Object>> supplierMap = iSupplierService.queryDataMationForMapByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(supplierIds));
            projects.forEach(project -> {
                if (StrUtil.equals(CorrespondentEnterEnum.SUPPLIER.getKey(), project.getHolderKey())) {
                    Map<String, Object> tempMap = supplierMap.get(project.getHolderId());
                    tempMap.put("name", tempMap.get("title"));
                    project.setHolderMation(tempMap);
                }
            });
        }
        // 客户
        List<String> customerIds = projects.stream()
            .filter(project -> StrUtil.equals(CorrespondentEnterEnum.CUSTOM.getKey(), project.getHolderKey()))
            .map(Project::getHolderId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(customerIds)) {
            Map<String, Map<String, Object>> customerMap = iCustomerService.queryDataMationForMapByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(customerIds));
            projects.forEach(project -> {
                if (StrUtil.equals(CorrespondentEnterEnum.CUSTOM.getKey(), project.getHolderKey())) {
                    Map<String, Object> tempMap = customerMap.get(project.getHolderId());
                    tempMap.put("name", tempMap.get("title"));
                    project.setHolderMation(tempMap);
                }
            });
        }
        return projects;
    }

    /**
     * 开始执行项目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void executeProjectById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Project bean = selectById(id);
        // 执行中状态下可以完善成果与总结信息
        if (StrUtil.equals(bean.getState(), FlowableStateEnum.PASS.getKey())) {
            UpdateWrapper<Project> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Project::getState), ProjectStateEnum.EXECUTING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 信息完善
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void perfectProjectById(InputObject inputObject, OutputObject outputObject) {
        Project project = inputObject.getParams(Project.class);
        Project bean = selectById(project.getId());
        // 执行中或者已完成状态下可以完善成果与总结信息
        if (StrUtil.equals(bean.getState(), ProjectStateEnum.EXECUTING.getKey())
            || StrUtil.equals(bean.getState(), ProjectStateEnum.COMPLETED.getKey())) {
            UpdateWrapper<Project> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, project.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(Project::getActualStartTime), project.getActualStartTime());
            updateWrapper.set(MybatisPlusUtil.toColumns(Project::getActualEndTime), project.getActualEndTime());
            updateWrapper.set(MybatisPlusUtil.toColumns(Project::getState), ProjectStateEnum.COMPLETED.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(Project::getResultsContent), project.getResultsContent());
            update(updateWrapper);
            refreshCache(project.getId());
        } else {
            outputObject.setreturnMessage("数据状态已改变，请刷新页面！");
        }
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
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

    @Override
    public void queryProProjectList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.equals(commonPageInfo.getType(), "myCharge")) {
            // 我负责的
            ResultEntity resultEnt = iTeamBusinessService.queryMyBusinessTeamIdsLinkObjectId(commonPageInfo.getPage(),
                commonPageInfo.getLimit(), getServiceClassName(), true);
            if (CollectionUtil.isEmpty(resultEnt.getRows())) {
                return;
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

            // 设置往来单位信息
            iSupplierService.setDataMation(projectList, Project::getHolderId);
            iCustomerService.setDataMation(projectList, Project::getHolderId);
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
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            // 根据客户/供应商查询
            queryWrapper.eq(MybatisPlusUtil.toColumns(Project::getHolderId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);

        // 设置往来单位信息
        iSupplierService.setMationForMap(beans, "holderId", "holderMation");
        iCustomerService.setMationForMap(beans, "holderId", "holderMation");
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
        return project;
    }

    @Override
    public List<Project> selectByIds(String... ids) {
        List<Project> projects = super.selectByIds(ids);
        // 部门
        iDepmentService.setDataMation(projects, Project::getDepartmentId);
        // 设置往来单位信息
        iSupplierService.setDataMation(projects, Project::getHolderId);
        iCustomerService.setDataMation(projects, Project::getHolderId);
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

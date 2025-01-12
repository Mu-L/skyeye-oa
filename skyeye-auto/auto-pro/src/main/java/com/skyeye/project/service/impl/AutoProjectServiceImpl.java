/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exception.CustomException;
import com.skyeye.product.service.AutoProductService;
import com.skyeye.project.dao.AutoProjectDao;
import com.skyeye.project.entity.AutoProject;
import com.skyeye.project.entity.AutoProjectQueryDo;
import com.skyeye.project.service.AutoProjectService;
import com.skyeye.sdk.catalog.service.CatalogSdkService;
import com.skyeye.team.service.ITeamBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoProjectServiceImpl
 * @Description: 项目管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/20 19:28
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目管理", groupName = "项目管理")
public class AutoProjectServiceImpl extends SkyeyeBusinessServiceImpl<AutoProjectDao, AutoProject> implements AutoProjectService, CatalogSdkService {

    @Autowired
    private ITeamBusinessService iTeamBusinessService;

    @Autowired
    private AutoProductService autoProductService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        AutoProjectQueryDo projectQueryDo = inputObject.getParams(AutoProjectQueryDo.class);
        if (StrUtil.equals("myCharge", projectQueryDo.getType())) {
            // 我负责的
            List<String> ids = iTeamBusinessService.queryMyBusinessTeamIdsLinkObjectId(projectQueryDo.getPage(),
                projectQueryDo.getLimit(), getServiceClassName());
            if (CollectionUtil.isEmpty(ids)) {
                throw new CustomException("您还不在任何团队中，请联系管理员");
            }
            projectQueryDo.setIds(ids);
        }
        String userId = inputObject.getLogParams().get("id").toString();
        projectQueryDo.setCreateId(userId);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoProjectList(projectQueryDo);
        return beans;
    }

    @Override
    public void createPostpose(AutoProject entity, String userId) {
        // 创建团队信息
        iTeamBusinessService.createTeamBusiness(entity.getTeamTemplateId(), entity.getId(), getServiceClassName());
    }

    @Override
    public void deletePostpose(String id) {
        iTeamBusinessService.deleteTeamBusiness(id, getServiceClassName());
    }

    @Override
    public AutoProject selectById(String id) {
        AutoProject autoProject = super.selectById(id);
        autoProductService.setDataMation(autoProject, AutoProject::getProductId);
        return autoProject;
    }

    @Override
    public void queryAllAutoProjectList(InputObject inputObject, OutputObject outputObject) {
        String type = inputObject.getParams().get("type").toString();
        AutoProjectQueryDo projectQueryDo = new AutoProjectQueryDo();
        projectQueryDo.setType(type);
        String userId = inputObject.getLogParams().get("id").toString();
        projectQueryDo.setCreateId(userId);
        if (StrUtil.equals("myCharge", projectQueryDo.getType())) {
            List<String> teamTemplateIds = iTeamBusinessService.getMyTeamIds();
            if (CollectionUtil.isEmpty(teamTemplateIds)) {
                // 查询是我负责的并且我没有在任何团队的时候，直接返回
                return;
            }
            projectQueryDo.setTeamTemplateIds(teamTemplateIds);
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoProjectList(projectQueryDo);
        String serviceClassName = getServiceClassName();
        beans.forEach(bean -> {
            bean.put("serviceClassName", serviceClassName);
        });
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}

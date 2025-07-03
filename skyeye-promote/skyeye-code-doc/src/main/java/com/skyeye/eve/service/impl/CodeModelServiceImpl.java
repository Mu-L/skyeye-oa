/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.dao.CodeModelDao;
import com.skyeye.eve.entity.codedoc.model.CodeModelQueryDo;
import com.skyeye.eve.service.CodeModelService;
import com.skyeye.eve.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CodeModelServiceImpl
 * @Description: 模板信息管理服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "模板信息", groupName = "模板信息", tenant = TenantEnum.PLATE)
public class CodeModelServiceImpl implements CodeModelService {

    @Autowired
    private CodeModelDao codeModelDao;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 获取模板列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @IgnoreTenant
    public void queryCodeModelList(InputObject inputObject, OutputObject outputObject) {
        CodeModelQueryDo codeModelQuery = inputObject.getParams(CodeModelQueryDo.class);
        String tenantId = TenantContext.getTenantId();
        if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
            throw new IllegalArgumentException("非平台租户不能访问");
        }
        codeModelQuery.setTenantId(tenantId);
        Page pages = PageHelper.startPage(codeModelQuery.getPage(), codeModelQuery.getLimit());
        List<Map<String, Object>> beans = codeModelDao.queryCodeModelList(codeModelQuery);
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        iAuthUserService.setNameForMap(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增模板列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertCodeModelMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        Map<String, Object> checkBean = codeModelDao.queryCodeModelMationByName(map);
        if (CollectionUtil.isEmpty(checkBean)) {
            DataCommonUtil.setCommonData(map, inputObject.getLogParams().get("id").toString());
            codeModelDao.insertCodeModelMation(map);
        } else {
            outputObject.setreturnMessage("该模板已存在，请更换。");
        }
    }

    /**
     * 删除模板信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteCodeModelById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        codeModelDao.deleteCodeModelById(map);
    }

    /**
     * 编辑模板信息时进行回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCodeModelMationToEditById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        Map<String, Object> bean = codeModelDao.queryCodeModelMationToEditById(map);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 编辑模板信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editCodeModelMationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        Map<String, Object> checkBean = codeModelDao.queryCodeModelMationByIdAndName(map);
        if (CollectionUtil.isEmpty(checkBean)) {
            Map<String, Object> user = inputObject.getLogParams();
            map.put("lastUpdateId", user.get("id"));
            map.put("lastUpdateTime", DateUtil.getTimeAndToString());
            codeModelDao.editCodeModelMationById(map);
        } else {
            outputObject.setreturnMessage("该模板已存在，请更换。");
        }
    }

}

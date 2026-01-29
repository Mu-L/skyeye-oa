/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.supplier.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.entity.SupplierContract;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.exception.CustomException;
import com.skyeye.sdk.catalog.service.CatalogSdkService;
import com.skyeye.supplier.dao.SupplierDao;
import com.skyeye.supplier.entity.Supplier;
import com.skyeye.supplier.service.SupplierService;
import com.skyeye.team.service.ITeamBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SupplierServiceImpl
 * @Description: 供应商信息管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "供应商管理", groupName = "供应商管理")
public class SupplierServiceImpl extends SkyeyeBusinessServiceImpl<SupplierDao, Supplier> implements SupplierService, CatalogSdkService {

    @Autowired
    private ITeamBusinessService iTeamBusinessService;

    @Autowired
    private SupplierContractService supplierContractService;

    @Override
    public void querySupplierList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.equals(commonPageInfo.getType(), "myCharge")) {
            // 我负责的
            ResultEntity resultEnt = iTeamBusinessService.queryMyBusinessTeamIdsLinkObjectId(commonPageInfo.getPage(),
                commonPageInfo.getLimit(), getServiceClassName(), true);
            if (CollectionUtil.isEmpty(resultEnt.getRows())) {
                return;
            }
            List<String> ids = resultEnt.getRows().stream().map(row -> row.get("objectId").toString()).distinct().collect(Collectors.toList());
            QueryWrapper<Supplier> queryWrapper = getQueryWrapper(commonPageInfo);
            queryWrapper.in(CommonConstants.ID, ids);
            List<Supplier> supplierList = list(queryWrapper);
            iAuthUserService.setName(supplierList, "createId", "createName");
            iAuthUserService.setName(supplierList, "lastUpdateId", "lastUpdateName");
            String serviceClassName = getServiceClassName();
            supplierList.forEach(supplier -> {
                supplier.setServiceClassName(serviceClassName);
            });
            outputObject.setBeans(supplierList);
            outputObject.settotal(resultEnt.getTotal());
        } else {
            // 我创建的 / 全部
            queryPageList(inputObject, outputObject);
        }
    }

    @Override
    public QueryWrapper<Supplier> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Supplier> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "myCreate")) {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(Supplier::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        }
        return queryWrapper;
    }

    @Override
    public void createPostpose(Supplier entity, String userId) {
        // 创建团队信息
        iTeamBusinessService.createTeamBusiness(entity.getTeamTemplateId(), entity.getId(), getServiceClassName());
    }

    @Override
    public void deletePreExecution(String id) {
        // 获取与供应商相关的合同列表
        List<SupplierContract> beans = supplierContractService.querySupplierContractListByObjectId(id);
        if (CollectionUtil.isNotEmpty(beans)) {
            throw new CustomException("存在合同信息，无法删除.");
        }
    }

    @Override
    public void deletePostpose(String id) {
        iTeamBusinessService.deleteTeamBusiness(id, getServiceClassName());
    }

    @Override
    public void queryAllSupplierList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Supplier::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Supplier::getCreateTime));
        List<Supplier> supplierList = list(queryWrapper);
        outputObject.setBeans(supplierList);
        outputObject.settotal(supplierList.size());
    }

}

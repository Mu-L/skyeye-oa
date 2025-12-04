/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.customer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.entity.CrmContract;
import com.skyeye.contract.service.CrmContractService;
import com.skyeye.customer.dao.CustomerDao;
import com.skyeye.customer.entity.CustomerMation;
import com.skyeye.customer.entity.CustomerQueryDo;
import com.skyeye.customer.service.CustomerService;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.exception.CustomException;
import com.skyeye.opportunity.entity.CrmOpportunity;
import com.skyeye.opportunity.service.CrmOpportunityService;
import com.skyeye.sdk.catalog.service.CatalogSdkService;
import com.skyeye.team.service.ITeamBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CustomerServiceImpl
 * @Description: 客户信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "客户管理", groupName = "客户管理")
public class CustomerServiceImpl extends SkyeyeBusinessServiceImpl<CustomerDao, CustomerMation> implements CustomerService, CatalogSdkService {

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Autowired
    private ITeamBusinessService iTeamBusinessService;

    @Autowired
    private CrmOpportunityService crmOpportunityService;

    @Autowired
    private CrmContractService crmContractService;

    @Override
    public void queryCustomerList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.equals(commonPageInfo.getType(), "myCharge")) {
            // 我负责的
            ResultEntity resultEnt = iTeamBusinessService.queryMyBusinessTeamIdsLinkObjectId(commonPageInfo.getPage(),
                commonPageInfo.getLimit(), getServiceClassName(), true);
            if (CollectionUtil.isEmpty(resultEnt.getRows())) {
                return;
            }
            List<String> ids = resultEnt.getRows().stream().map(row -> row.get("objectId").toString()).distinct().collect(Collectors.toList());
            QueryWrapper<CustomerMation> queryWrapper = getQueryWrapper(commonPageInfo);
            queryWrapper.in(CommonConstants.ID, ids);
            List<CustomerMation> customerMationList = list(queryWrapper);
            iAuthUserService.setName(customerMationList, "createId", "createName");
            iAuthUserService.setName(customerMationList, "lastUpdateId", "lastUpdateName");
            String serviceClassName = getServiceClassName();
            customerMationList.forEach(customerMation -> {
                customerMation.setServiceClassName(serviceClassName);
            });
            outputObject.setBeans(customerMationList);
            outputObject.settotal(resultEnt.getTotal());
        } else {
            // 我创建的 / 全部
            queryPageList(inputObject, outputObject);
        }
    }

    @Override
    public QueryWrapper<CustomerMation> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<CustomerMation> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "myCreate")) {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(CustomerMation::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("typeId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CustomerMation::getTypeId), commonPageInfo.getCustomParamsMapStr("typeId"));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("fromId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CustomerMation::getFromId), commonPageInfo.getCustomParamsMapStr("fromId"));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("industryId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CustomerMation::getIndustryId), commonPageInfo.getCustomParamsMapStr("industryId"));
        }
        return queryWrapper;
    }

    @Override
    public void createPostpose(CustomerMation entity, String userId) {
        // 创建团队信息
        iTeamBusinessService.createTeamBusiness(entity.getTeamTemplateId(), entity.getId(), getServiceClassName());
    }

    @Override
    public void deletePreExecution(String id) {
        // 获取与客户相关的合同列表
        List<CrmContract> beans = crmContractService.queryCrmContractListByObjectId(id);
        if (CollectionUtil.isNotEmpty(beans)) {
            throw new CustomException("存在合同信息，无法删除.");
        }
        // 获取与客户相关的商机列表
        List<CrmOpportunity> list = crmOpportunityService.queryCrmOpportunityListByObjectId(id);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new CustomException("存在商机信息，无法删除.");
        }
    }

    @Override
    public void deletePostpose(String id) {
        iTeamBusinessService.deleteTeamBusiness(id, getServiceClassName());
    }

    /**
     * 获取公海客户群列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryInternationalCustomerList(InputObject inputObject, OutputObject outputObject) {
        CustomerQueryDo customerQuery = inputObject.getParams(CustomerQueryDo.class);
        Map<String, Object> settings = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
        customerQuery.setNoDocumentaryDayNum(settings.get("noDocumentaryDayNum").toString());
        customerQuery.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        Page pages = PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());

        List<Map<String, Object>> beans = skyeyeBaseMapper.queryInternationalCustomerList(customerQuery);
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        iAuthUserService.setNameForMap(beans, "lastUpdateId", "lastUpdateName");
        String serviceClassName = getServiceClassName();
        beans.forEach(bean -> {
            bean.put("serviceClassName", serviceClassName);
        });
        outputObject.setBean(customerQuery);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

}

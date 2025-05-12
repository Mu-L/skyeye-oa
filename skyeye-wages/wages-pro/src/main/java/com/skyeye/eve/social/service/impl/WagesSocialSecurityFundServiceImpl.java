/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.ApplicableObjectsType;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.social.dao.WagesSocialSecurityFundDao;
import com.skyeye.eve.social.entity.ApplicableObjects;
import com.skyeye.eve.social.entity.SocialSecurityFund;
import com.skyeye.eve.social.service.ApplicableObjectsService;
import com.skyeye.eve.social.service.WagesSocialSecurityFundService;
import com.skyeye.organization.service.ICompanyService;
import com.skyeye.organization.service.IDepmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: WagesSocialSecurityFundServiceImpl
 * @Description: 社保公积金服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 8:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "社保公积金管理", groupName = "社保公积金管理")
public class WagesSocialSecurityFundServiceImpl extends SkyeyeBusinessServiceImpl<WagesSocialSecurityFundDao, SocialSecurityFund> implements WagesSocialSecurityFundService {

    @Autowired
    private ApplicableObjectsService applicableObjectsService;

    @Autowired
    private ICompanyService iCompanyService;

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryWagesSocialSecurityFundList(pageInfo);
        return beans;
    }

    @Override
    public void writePostpose(SocialSecurityFund entity, String userId) {
        super.writePostpose(entity, userId);
        applicableObjectsService.saveApplicableObjects(entity.getId(), entity.getApplicableObjectsList());
    }

    @Override
    public void deletePostpose(String id) {
        applicableObjectsService.deleteApplicableObjectsByPId(id);
    }

    @Override
    public SocialSecurityFund getDataFromDb(String id) {
        SocialSecurityFund securityFund = super.getDataFromDb(id);
        // 适用对象信息
        List<ApplicableObjects> applicableObjectsList = applicableObjectsService.queryApplicableObjectsByPId(id);
        securityFund.setApplicableObjectsList(applicableObjectsList);
        return securityFund;
    }

    @Override
    public SocialSecurityFund selectById(String id) {
        SocialSecurityFund securityFund = super.selectById(id);
        // 设置适用对象信息
        List<ApplicableObjects> applicableObjectsList = securityFund.getApplicableObjectsList();
        if (CollectionUtil.isNotEmpty(applicableObjectsList)) {
            Map<Integer, List<ApplicableObjects>> listMap = applicableObjectsList.stream()
                .collect(Collectors.groupingBy(ApplicableObjects::getObjectType));
            listMap.forEach((key, value) -> {
                List<String> ids = value.stream().map(ApplicableObjects::getObjectId).collect(Collectors.toList());
                if (ApplicableObjectsType.STAFF.getKey().equals(key)) {
                    // 员工
                    Map<String, Map<String, Object>> staffMaps = iAuthUserService.queryUserMationListByStaffIds(ids);
                    setObjectMation(applicableObjectsList, staffMaps, key);
                } else if (ApplicableObjectsType.DEPARTMENT.getKey().equals(key)) {
                    // 部门
                    String departmentIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(ids);
                    Map<String, Map<String, Object>> departMent = iDepmentService.queryDataMationForMapByIds(departmentIdStr);
                    setObjectMation(applicableObjectsList, departMent, key);
                } else if (ApplicableObjectsType.COMPANY.getKey().equals(key)) {
                    // 企业
                    String companyIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(ids);
                    Map<String, Map<String, Object>> company = iCompanyService.queryDataMationForMapByIds(companyIdStr);
                    setObjectMation(applicableObjectsList, company, key);
                }
            });
        }
        return securityFund;
    }

    private static void setObjectMation(List<ApplicableObjects> applicableObjectsList, Map<String, Map<String, Object>> temMap, Integer key) {
        if (CollectionUtil.isEmpty(temMap)) {
            return;
        }
        applicableObjectsList.forEach(applicableObjects -> {
            if (key.equals(applicableObjects.getObjectType())) {
                applicableObjects.setObjectMation(temMap.get(applicableObjects.getObjectId()));
            }
        });
    }

    @Override
    public List<SocialSecurityFund> querySocialSecurityFundByDate(String date) {
        if (StrUtil.isEmpty(date)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<SocialSecurityFund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SocialSecurityFund::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(SocialSecurityFund::getStartTime) + ", '%Y-%m') <= date_format({0}, '%Y-%m')", date)
            .apply("date_format(" + MybatisPlusUtil.toColumns(SocialSecurityFund::getEndTime) + ", '%Y-%m') >= date_format({0}, '%Y-%m')", date);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SocialSecurityFund::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<SocialSecurityFund> socialSecurityFundList = list(queryWrapper);
        List<String> ids = socialSecurityFundList.stream().map(SocialSecurityFund::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        // 获取适用对象
        Map<String, List<ApplicableObjects>> listMap = applicableObjectsService.queryApplicableObjectsByPId(ids);
        socialSecurityFundList.forEach(socialSecurityFund -> {
            socialSecurityFund.setApplicableObjectsList(listMap.get(socialSecurityFund.getId()));
        });
        return socialSecurityFundList;
    }
}

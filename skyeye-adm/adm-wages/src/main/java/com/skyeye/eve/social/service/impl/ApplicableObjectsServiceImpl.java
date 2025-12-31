/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.social.dao.ApplicableObjectsDao;
import com.skyeye.eve.social.entity.ApplicableObjects;
import com.skyeye.eve.social.service.ApplicableObjectsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ApplicableObjectsServiceImpl
 * @Description: 社保公积金适用对象服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 8:46
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "社保公积金适用对象管理", groupName = "社保公积金适用对象管理", manageShow = false)
public class ApplicableObjectsServiceImpl extends SkyeyeBusinessServiceImpl<ApplicableObjectsDao, ApplicableObjects> implements ApplicableObjectsService {

    @Override
    public void deleteApplicableObjectsByPId(String securityFundId) {
        QueryWrapper<ApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApplicableObjects::getSecurityFundId), securityFundId);
        remove(queryWrapper);
    }

    @Override
    public void saveApplicableObjects(String securityFundId, List<ApplicableObjects> applicableObjectsList) {
        deleteApplicableObjectsByPId(securityFundId);
        if (CollectionUtil.isNotEmpty(applicableObjectsList)) {
            for (ApplicableObjects applicableObjects : applicableObjectsList) {
                applicableObjects.setSecurityFundId(securityFundId);
            }
            createEntity(applicableObjectsList, StrUtil.EMPTY);
        }
    }

    @Override
    public List<ApplicableObjects> queryApplicableObjectsByPId(String securityFundId) {
        QueryWrapper<ApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApplicableObjects::getSecurityFundId), securityFundId);
        List<ApplicableObjects> applicableObjectsList = list(queryWrapper);
        return applicableObjectsList;
    }

    @Override
    public Map<String, List<ApplicableObjects>> queryApplicableObjectsByPId(List<String> securityFundId) {
        if (CollectionUtil.isEmpty(securityFundId)) {
            return Collections.emptyMap();
        }
        QueryWrapper<ApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ApplicableObjects::getSecurityFundId), securityFundId);
        List<ApplicableObjects> applicableObjectsList = list(queryWrapper);
        Map<String, List<ApplicableObjects>> listMap = applicableObjectsList.stream()
            .collect(Collectors.groupingBy(ApplicableObjects::getSecurityFundId));
        return listMap;
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dao.ApiMationDao;
import com.skyeye.eve.entity.api.ApiMation;
import com.skyeye.exception.CustomException;
import com.skyeye.service.ApiMationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @ClassName: ApiMationServiceImpl
 * @Description: api接口服务类
 * @author: skyeye云系列
 * @date: 2021/11/28 12:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "api接口参数", groupName = "api接口参数")
public class ApiMationServiceImpl extends SkyeyeBusinessServiceImpl<ApiMationDao, ApiMation> implements ApiMationService {

    @Override
    protected void validatorEntity(ApiMation entity) {
        QueryWrapper<ApiMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApiMation::getAppId), entity.getAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApiMation::getRequestUrl), entity.getRequestUrl());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApiMation::getTitle), entity.getTitle());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        ApiMation checkDictTypeMation = getOne(queryWrapper, false);
        if (ObjectUtils.isEmpty(checkDictTypeMation)) {
            throw new CustomException("this data [title] is non-existent.");
        }
    }

    @Override
    protected void deletePreExecution(String id) {
        UpdateWrapper<ApiMation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        String ip = ToolUtil.getIpByRequest(PutObject.getRequest());
        updateWrapper.set(MybatisPlusUtil.toColumns(ApiMation::getDeleteIp), ip);
        update(updateWrapper);
    }

    @Override
    public List<ApiMation> queryApiMationByAppIdAndUrlId(String appId, String urlId) {
        QueryWrapper<ApiMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApiMation::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApiMation::getRequestUrl), urlId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ApiMation::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        List<ApiMation> apiMations = list(queryWrapper);
        return apiMations;
    }

}


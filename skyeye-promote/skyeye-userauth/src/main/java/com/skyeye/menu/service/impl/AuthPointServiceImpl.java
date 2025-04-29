/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.menu.classenum.MenuPointType;
import com.skyeye.menu.dao.AuthPointDao;
import com.skyeye.menu.entity.AuthPoint;
import com.skyeye.menu.service.AuthPointService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AuthPointServiceImpl
 * @Description: 菜单权限点管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/7/23 19:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "权限点管理", groupName = "菜单管理", teamAuth = true, tenant = TenantEnum.PLATE)
public class AuthPointServiceImpl extends SkyeyeBusinessServiceImpl<AuthPointDao, AuthPoint> implements AuthPointService {

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        TableSelectInfo selectInfo = inputObject.getParams(TableSelectInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMenuAuthPointList(selectInfo);
        beans.forEach(bean -> {
            bean.put("typeName", MenuPointType.getTypeName(Integer.parseInt(bean.get("type").toString())));
        });
        return beans;
    }

    @Override
    protected void createPrepose(AuthPoint entity) {
        entity.setMenuNum(String.valueOf(DateUtil.getTimeStampAndToString()));
    }

    @Override
    protected void validatorEntity(AuthPoint entity) {
        super.validatorEntity(entity);
        QueryWrapper<AuthPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(AuthPoint::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(AuthPoint::getAuthMenu), entity.getAuthMenu()));
        queryWrapper.eq(MybatisPlusUtil.toColumns(AuthPoint::getObjectId), entity.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AuthPoint::getParentId), entity.getParentId());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AuthPoint checkSysMenuAuthPoint = getOne(queryWrapper);

        if (!ObjectUtils.isEmpty(checkSysMenuAuthPoint)) {
            throw new CustomException("该菜单下已存在相同的名称/接口URL，请进行更改。");
        }
    }

    @Override
    @IgnoreTenant
    public List<AuthPoint> selectByIds(String... ids) {
        return super.selectByIds(ids);
    }
}

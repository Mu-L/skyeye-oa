/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.mail.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.mail.dao.MailTypeDao;
import com.skyeye.eve.mail.entity.MailType;
import com.skyeye.eve.mail.service.MailTypeService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: MailTypeServiceImpl
 * @Description: 通讯录分组管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/23 12:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "通讯录分组管理", groupName = "通讯录分组管理")
public class MailTypeServiceImpl extends SkyeyeBusinessServiceImpl<MailTypeDao, MailType> implements MailTypeService {

    @Override
    protected QueryWrapper<MailType> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MailType> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MailType::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(MailType entity) {
        super.validatorEntity(entity);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<MailType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MailType::getName), entity.getName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(MailType::getCreateId), userId);
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        MailType checkMailType = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkMailType)) {
            throw new CustomException("this data['name'] is exist.");
        }
    }

    /**
     * 获取我的通讯录类型用作下拉框展示
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllMailTypeList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<MailType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MailType::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(MailType::getCreateTime));
        List<MailType> mailTypeList = list(queryWrapper);
        outputObject.setBeans(mailTypeList);
        outputObject.settotal(mailTypeList.size());
    }

}

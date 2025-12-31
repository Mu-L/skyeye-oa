/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.subject.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.subject.dao.IfsAccountSubjectDao;
import com.skyeye.subject.entity.AccountSubject;
import com.skyeye.subject.service.IfsAccountSubjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * @ClassName: IfsAccountSubjectServiceImpl
 * @Description: 会计科目管理服务类
 * @author: skyeye云系列
 * @date: 2021/11/27 12:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "会计科目管理", groupName = "会计科目管理")
public class IfsAccountSubjectServiceImpl extends SkyeyeBusinessServiceImpl<IfsAccountSubjectDao, AccountSubject> implements IfsAccountSubjectService {

    @Override
    public void validatorEntity(AccountSubject entity) {
        super.validatorEntity(entity);
        // 校验基础信息
        QueryWrapper<AccountSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AccountSubject::getNum), entity.getNum());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AccountSubject checkMation = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkMation)) {
            throw new CustomException("this 【num】 is exist.");
        }
    }

    @Override
    public void queryEnabledSubjectList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<AccountSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AccountSubject::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<AccountSubject> accountSubjectList = list(queryWrapper);
        accountSubjectList.forEach(accountSubject -> {
            accountSubject.setName(String.format(Locale.ROOT, "%s_%s", accountSubject.getNum(), accountSubject.getName()));
        });
        outputObject.setBeans(accountSubjectList);
        outputObject.settotal(accountSubjectList.size());
    }
}


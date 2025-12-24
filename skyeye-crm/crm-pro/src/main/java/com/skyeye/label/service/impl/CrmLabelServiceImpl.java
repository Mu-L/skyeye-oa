/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.label.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.label.dao.CrmLabelDao;
import com.skyeye.label.entity.CrmLabel;
import com.skyeye.label.service.CrmLabelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: CrmLabelServiceImpl
 * @Description: CRM客户标签服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "CRM客户标签", groupName = "CRM客户标签")
public class CrmLabelServiceImpl extends SkyeyeBusinessServiceImpl<CrmLabelDao, CrmLabel> implements CrmLabelService {

    @Override
    public void queryEnabledLabelList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<CrmLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CrmLabel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(CrmLabel::getCreateTime));
        List<CrmLabel> labelList = list(queryWrapper);
        outputObject.setBeans(labelList);
        outputObject.settotal(labelList.size());
    }

}


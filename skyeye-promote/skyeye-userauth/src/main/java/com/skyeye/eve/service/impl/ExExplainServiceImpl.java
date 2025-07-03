/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.ExExplainDao;
import com.skyeye.eve.entity.explain.ExExplain;
import com.skyeye.eve.service.ExExplainService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: ExExplainServiceImpl
 * @Description: 说明设置服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/22 14:42
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "说明设置", groupName = "说明设置", tenant = TenantEnum.PLATE)
public class ExExplainServiceImpl extends SkyeyeBusinessServiceImpl<ExExplainDao, ExExplain> implements ExExplainService {

    @Override
    public void queryExExplainByType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Integer type = Integer.parseInt(map.get("type").toString());
        QueryWrapper<ExExplain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExExplain::getType), type);
        ExExplain exExplain = getOne(queryWrapper, false);
        outputObject.setBean(exExplain);
    }

}
